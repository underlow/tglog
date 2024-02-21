package me.underlow.tglog.messages

import LogEventConfiguration
import mu.KotlinLogging

/**
 * Filters messages by message substring
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if string from exclude list is in message, message is excluded
 * 2. if include list is empty message is included (no filtering for empty include list)
 * 2. if include list is not empty and message contains some string from include list, message is excluded
 *
 * @return true if message should be included, false if message should be excluded
 */
class MessageSubstringFilter(private val configuration: LogEventConfiguration) {

    private val include = configuration.include.split(",").filter { it.isNotBlank() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.toSet()

    fun filter(message: LogMessage): Boolean {
        if (exclude.isNotEmpty() && exclude.any { it in message.message }
        ) {
            logger.trace { "Message ${message.message} is excluded from processing" }
            return false
        }
        if (include.isNotEmpty() && include.all { it !in message.message }) {
            logger.trace { "Message ${message.message} is not included in processing" }
            return false
        }
        return true
    }
}

private val logger = KotlinLogging.logger { }
