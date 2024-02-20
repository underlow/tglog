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
import org.springframework.stereotype.Service

@Service
class DockerService {
    init {
        val dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build()

        val dockerHttpClient = ZerodepDockerHttpClient.Builder().dockerHost(dockerClientConfig.dockerHost).build()

        val dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient)
        // List all containers
        val containers = dockerClient.listContainersCmd().exec()

        // Create an event listener
        val callback = ContainerLifecycleListener(dockerClient)

        // Iterate through containers and get logs
        for (container in containers) {
            attachLogListener(container, dockerClient)
        }
        dockerClient.eventsCmd().exec(callback).awaitCompletion()

    }


}

private fun attachLogListener(
    container: Container,
    dockerClient: DockerClient
) {
    val containerId = container.id
    println("attachLogListener to Container ID: ${container.readableName()}")

    // Get logs of the container
    val currentTimeSeconds = System.currentTimeMillis() / 1000
    val logContainerCmd: LogContainerCmd = dockerClient.logContainerCmd(containerId)
        .withStdOut(true)
        .withStdErr(true)
        .withFollowStream(true)
        .withSince(currentTimeSeconds.toInt())

    val callback = object : Adapter<Frame>() {
        override fun onNext(frame: Frame) {
//            String.format("%s: %s", streamType, String(payload).trim { it <= ' ' })
            println("${container.readableName()}:  ${frame.payload.decodeToString()}")
        }
    }

    logContainerCmd.exec(callback)//.awaitCompletion()

    println("=====================================")
}


private class ContainerLifecycleListener(val dockerClient: DockerClient) : Adapter<Event>() {
    override fun onNext(event: Event) {
        println("Received event: ${event.type}, ${event.action}, ${event.actor?.attributes}")

        // Check if the event is a container creation event
        if (event.type == EventType.CONTAINER && event.action == "start") {
            val containerId = event.actor?.id ?: error("Container ID is null")
            val container = dockerClient.listContainersCmd().exec().firstOrNull { it.id == containerId }

            if (container == null) {
                println("Container might be removed: $containerId")
                return
            }

            println("New container created: ${container.readableName()}")

            // Perform actions based on container creation event
            // For example, retrieve container details, inspect it, or perform additional tasks
            attachLogListener(container, dockerClient)
        }
    }
}

private fun Container.readableName() = names.joinToString(",") { it.removePrefix("/") }
