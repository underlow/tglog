package me.underlow.tglog.docker

import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader


@Service
class RuntimeDockerParameters {

    val runningInDocker: Boolean = System.getenv("DOCKER_CONTAINER") == "true"
    val dockerHost: String = System.getenv("DOCKER_HOST") ?: "localhost"
    val containerId = getContainerIdFromRuntime()

    init {
        logger.info { "Running in docker: $runningInDocker, docker host: $dockerHost, containerName: $containerId" }
    }

    fun getContainerIdFromRuntime(): String {
        val proc = Runtime.getRuntime().exec("hostname")
        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        val containerName = reader.readLine()
        println("Container Name: $containerName")

        if (runningInDocker && (containerName == null || containerName.isBlank())) {
            logger.error { "Running in Docker but cannot get container name, exiting..." }
        }

        return containerName
    }
}

private val logger = KotlinLogging.logger { }
