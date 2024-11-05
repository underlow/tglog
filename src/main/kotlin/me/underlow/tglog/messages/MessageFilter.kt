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
        // checking inverted to prevent case when  container containerNameFilter.filter(message) returns false
        // and we fall into next check. if filter returns false we should immediately return false
        if (!containerNameFilter.filter(message))
            return false

        if (!containerEventFilter.filter(message))
            return false

        return true
    }

    fun filterLogMessage(message: LogMessage): Boolean {
        // check if we have container specific filters
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null) {
            return containerFilters.messageSubstring.filter(message)
        }

        // if no filter then by global filters
        // checking inverted to prevent case when  container containerNameFilter.filter(message) returns false
        // and we fall into next check. if filter returns false we should immediately return false
        if (!containerNameFilter.filter(message))
            return false

        if (!messageSubstringFilter.filter(message))
            return false

        return true
    }

    fun humanReadableFilters(): String {
        val containers = "<b>Containers:</b> \ninclude: ${containerNamesConfiguration.include} \nexclude: ${containerNamesConfiguration.exclude}"
        val logs = "<b>Logs:</b> \ninclude: ${logsEventConfiguration.include} \nexclude: ${logsEventConfiguration.exclude}"
        val containerEvents = "<b>Container events:</b> \ninclude: ${containerEventsConfiguration.include} \nexclude: ${containerEventsConfiguration.exclude}"

        val containerProperties = containersProperties.container.joinToString("\n") {
            "<b>Container: ${it.name}</b> \ninclude: ${it.container.events.include} \nexclude: ${it.container.events.exclude} \nlogs: \ninclude:${it.logs.events.include} \nexclude: ${it.logs.events.exclude}"
        }

        return "$containers\n$logs\n$containerEvents\n\n$containerProperties"
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
