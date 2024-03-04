package me.underlow.tglog.messages

import ContainerNamesConfiguration
import mu.KotlinLogging

/**
 * Filters messages by container name
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if container name is in exclude list, message is excluded
 * 2. if include list is empty message is excluded
 * 3. if include list is not empty and container name is not in include list, message is excluded
 * 4. if include list is '*' then all containers (except excluded) are included
 *
 * @return true if message should be included, false if message should be excluded
 */
class ContainerNameFilter(private val configuration: ContainerNamesConfiguration) {
    private val includeAll = configuration.include == "*"

    private val include = configuration.include.split(",").filter { it.isNotBlank() }.map { it.lowercase().trim() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.map { it.lowercase().trim() }.toSet()

    fun filter(message: Message): Boolean {
        if (message.containerName.lowercase() in exclude) {
            logger.debug { "Container ${message.containerName} is excluded from processing" }
            return false
        }

        if (includeAll) {
            return true
        }

        if (message.containerName.lowercase() in include) {
            return true
        }

        logger.debug { "Container ${message.containerName} is not included in processing" }
        return false
    }
}

private val logger = KotlinLogging.logger { }
