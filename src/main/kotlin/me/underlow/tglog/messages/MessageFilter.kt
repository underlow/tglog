package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service


@Service
@EnableConfigurationProperties(
    LogsEventConfiguration::class,
    ContainerEventsConfiguration::class,
    ContainerNamesConfiguration::class,
    ContainersProperties::class,
)
class MessageFilter(
    private val logsEventConfiguration: LogsEventConfiguration,
    private val containerEventsConfiguration: ContainerEventsConfiguration,
    private val containerNamesConfiguration: ContainerNamesConfiguration,
    private val containersProperties: ContainersProperties,
) {

    init {
        logger.debug { "logsEventConfiguration: $logsEventConfiguration" }
        logger.debug { "containerEventsConfiguration: $containerEventsConfiguration" }
        logger.debug { "containerNamesConfiguration: $containerNamesConfiguration" }
        logger.debug { "containersProperties: $containersProperties" }
    }

    private val containerNameFilter = ContainerNameFilter(containerNamesConfiguration)
    private val containerEventFilter = ContainerEventFilter(containerEventsConfiguration)
    private val messageSubstringFilter = MessageSubstringFilter(logsEventConfiguration)

    // map container name -> container filter
    private val containerFilters = containersProperties.container.associate { it.name to it.toContainerFilters() }

    fun filterContainerMessage(message: ContainerMessage): Boolean {
        // check if we have container specific filters
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null) {
            return containerFilters.containerEvents.filter(message)
        }

        // if no filter then by global filters
        if (containerNameFilter.filter(message))
            return true

        if (containerEventFilter.filter(message))
            return true

        return false
    }

    fun filterLogMessage(message: LogMessage): Boolean {
        // check if we have container specific filters
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null) {
            return containerFilters.messageSubstring.filter(message)
        }

        // if no filter then by global filters
        if (containerNameFilter.filter(message))
            return true

        if (messageSubstringFilter.filter(message))
            return true

        return false
    }
}

private fun ContainerProperties.toContainerFilters(): ContainerFilters {
    return ContainerFilters(
        containerEvents = ContainerEventFilter(this.container.events),
        messageSubstring = MessageSubstringFilter(this.logs.events),
    )
}

data class ContainerFilters(
    val containerEvents: ContainerEventFilter,
    val messageSubstring: MessageSubstringFilter,
)

private val logger = KotlinLogging.logger { }
