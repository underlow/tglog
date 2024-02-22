package me.underlow.tglog.messages

import ContainerEventsConfiguration
import mu.KotlinLogging

/**
 * Filters messages by container event
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if container event is in exclude list, message is excluded
 * 2. if include list is empty message is excluded
 * 3. if include list is not empty and container event is not in include list, message is excluded
 * 4. if include list is '*' then all containers (except excluded) are included
 *
 * @return true if message should be included, false if message should be excluded
 */
class ContainerEventFilter(private val configuration: ContainerEventsConfiguration) {
    private val includeAll = configuration.include == "*"

    private val include = configuration.include.split(",").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()

    fun filter(message: ContainerMessage): Boolean {
        if (message.event.lowercase() in exclude) {
            logger.debug { "Message ${message.event} is excluded from processing" }
            return false
        }

        if (includeAll)
            return true

        if (message.event.lowercase() in include) {
            return true
        }

        logger.debug { "Message ${message.event} is not included in processing" }

        return false
    }
}

private val logger = KotlinLogging.logger { }
