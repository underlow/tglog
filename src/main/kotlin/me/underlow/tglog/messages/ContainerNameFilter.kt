package me.underlow.tglog.messages

import ContainerNamesConfiguration
import mu.KotlinLogging

/**
 * Filters messages by container name
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if container name is in exclude list, message is excluded
 * 2. if include list is empty message is included (no filtering for empty include list)
 * 2. if include list is not empty and container name is not in include list, message is excluded
 *
 * @return true if message should be included, false if message should be excluded
 */
class ContainerNameFilter(private val configuration: ContainerNamesConfiguration) {
    private val include = configuration.include.split(",").filter { it.isNotBlank() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.toSet()

    fun filter(message: Message): Boolean {
        if (message.containerName in exclude) {
            logger.debug { "Container ${message.containerName} is excluded from processing" }
            return false
        }

        if (include.isNotEmpty() && message.containerName !in include
        ) {
            logger.debug { "Container ${message.containerName} is not included in processing" }
            return false
        }
        return true
    }
}

private val logger = KotlinLogging.logger { }
