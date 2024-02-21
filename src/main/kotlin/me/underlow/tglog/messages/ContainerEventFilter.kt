package me.underlow.tglog.messages

import ContainerEventsConfiguration
import mu.KotlinLogging

/**
 * Filters messages by container event
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if container event is in exclude list, message is excluded
 * 2. if include list is empty message is included (no filtering for empty include list)
 * 2. if include list is not empty and container event is not in include list, message is excluded
 *
 * @return true if message should be included, false if message should be excluded
 */
class ContainerEventFilter(private val configuration: ContainerEventsConfiguration) {
    private val include = configuration.include.split(",").filter { it.isNotBlank() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.toSet()

    fun filter(message: ContainerMessage): Boolean {
        return when {
            message.event in exclude -> {
                logger.debug { "Message ${message.event} is excluded from processing" }
                false
            }
            include.isNotEmpty() && message.event !in include -> {
                logger.debug { "Message ${message.event} is not included in processing" }
                false
            }
            else -> true
        }
    }
}

private val logger = KotlinLogging.logger { }
