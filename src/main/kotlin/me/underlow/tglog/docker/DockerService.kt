package me.underlow.tglog.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback.Adapter
import com.github.dockerjava.api.command.LogContainerCmd
import com.github.dockerjava.api.model.Container
import com.github.dockerjava.api.model.Event
import com.github.dockerjava.api.model.EventType
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient
import me.underlow.tglog.messages.ContainerMessage
import me.underlow.tglog.messages.LogMessage
import me.underlow.tglog.messages.MessageReceiver
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DockerService(val messageReceiver: MessageReceiver) {

    private val dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
    private val dockerHttpClient = ZerodepDockerHttpClient.Builder().dockerHost(dockerClientConfig.dockerHost).build()
    private val dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient)

    init {
        // List all containers
        val containers = dockerClient.listContainersCmd().exec()

        // Create an event listener
        val callback = object : Adapter<Event>() {
            override fun onNext(event: Event) {
                containerLifecycleListener(event)
            }
        }

        // Iterate through containers and get logs
        for (container in containers) {
            attachLogListener(container, dockerClient)
        }
        dockerClient.eventsCmd().exec(callback).awaitCompletion()

    }


    private fun attachLogListener(
        container: Container,
        dockerClient: DockerClient
    ) {
        val containerId = container.id
        logger.info { "attachLogListener to Container ID: ${container.readableName()}" }

        // Get logs of the container
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        val logContainerCmd: LogContainerCmd = dockerClient.logContainerCmd(containerId)
            .withStdOut(true)
            .withStdErr(true)
            .withFollowStream(true)
            .withSince(currentTimeSeconds.toInt())

        val callback = object : Adapter<Frame>() {
            override fun onNext(frame: Frame) {
                val message = LogMessage(container.readableName(), frame.payload.decodeToString().trim { it <= ' ' })
                logger.trace { "Received log message: $message" }
                messageReceiver.receiveMessage(message)
            }
        }

        logContainerCmd.exec(callback)//.awaitCompletion()
    }


    private fun containerLifecycleListener(event: Event) {
        logger.debug { "Received event: ${event.type}, ${event.action}, ${event.actor?.attributes}" }

        if (event.type != EventType.CONTAINER)
            return

        // Check if the event is a container creation event
        val containerId = event.actor?.id

        if (containerId == null) {
            logger.debug { "Container ID is null" }
            return
        }

        val container = dockerClient.listContainersCmd().exec().firstOrNull { it.id == containerId }

        if (container == null) {
            messageReceiver.receiveMessage(ContainerMessage(containerId, event.action ?: "unknown action"))
            logger.debug { "Container might be removed: $containerId" }
            return
        }

        messageReceiver.receiveMessage(ContainerMessage(container.readableName(), event.action ?: "unknown action"))

        if (event.action == "start") {
            logger.info { "New container created: ${container.readableName()}" }
            attachLogListener(container, dockerClient)
        }
    }
}

private fun Container.readableName() = names.joinToString(",") { it.removePrefix("/") }

private val logger = KotlinLogging.logger { }
