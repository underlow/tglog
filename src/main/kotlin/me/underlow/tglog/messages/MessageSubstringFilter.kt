package me.underlow.tglog.messages

import LogsEventConfiguration
import mu.KotlinLogging

/**
 * Filters messages by message substring
 * @param configuration configuration for container names
 * filtering rules are:
 * 1. if string from exclude list is in message, message is excluded
 * 2. if include list is empty message is excluded
 * 3. if include list is not empty and message contains some string from include list, message is excluded
 * 4. if include list is '*' then all messages (except excluded) are included
 *
 * @return true if message should be included, false if message should be excluded
 */
class MessageSubstringFilter(private val configuration: LogsEventConfiguration) {
    private val includeAll = configuration.include == "*"
    private val include = configuration.include.split(",").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()
    private val exclude = configuration.exclude.split(",").filter { it.isNotBlank() }.map { it.lowercase() }.toSet()

    fun filter(message: LogMessage): Boolean {
        if (exclude.isNotEmpty() && exclude.any { it in message.message.lowercase() }
        ) {
            logger.trace { "Message ${message.message} is excluded from processing" }
            return false
        }

        if (includeAll) {
            return true
        }

        if ( include.any { it in message.message.lowercase() }) {
            return true
        }

        logger.trace { "Message ${message.message} is not included in processing" }
        return false
    }
}

private val logger = KotlinLogging.logger { }
