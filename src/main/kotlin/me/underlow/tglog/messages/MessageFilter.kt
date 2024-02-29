package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.underlow.tglog.tg.TgMessage
import me.underlow.tglog.tg.TgSender
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
    private val messageQueue: MessageQueue,
    private val logsEventConfiguration: LogsEventConfiguration,
    private val containerEventsConfiguration: ContainerEventsConfiguration,
    private val containerNamesConfiguration: ContainerNamesConfiguration,
    private val containersProperties: ContainersProperties,
    private val tgSender: TgSender,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    init {
        logger.debug { "Starting message filter" }

        logger.debug { "logsEventConfiguration: $logsEventConfiguration" }
        logger.debug { "containerEventsConfiguration: $containerEventsConfiguration" }
        logger.debug { "containerNamesConfiguration: $containerNamesConfiguration" }
        logger.debug { "containersProperties: $containersProperties" }

        coroutineScope.launch {
            for (message in messageQueue.messageChannel) {
                when (message) {
                    is LogMessage -> processLogMessage(message)
                    is ContainerMessage -> processContainerMessage(message)
                    is HeartBeatMessage -> {} // should never really be here, heartbeat message couldn't be received from logs
                }
            }
        }
    }

    private val containerNameFilter = ContainerNameFilter(containerNamesConfiguration)
    private val containerEventFilter = ContainerEventFilter(containerEventsConfiguration)
    private val messageSubstringFilter = MessageSubstringFilter(logsEventConfiguration)

    // map container name -> container filter
    private val containerFilters = containersProperties.container.associate { it.name to it.toContainerFilters() }

    private fun processContainerMessage(message: ContainerMessage) {
        if (filterContainerMessage(message))
            return

        logger.debug { "Message ${TgMessage.readableMessage(message).take(100)} accepted by all filters" }
        coroutineScope.launch {
            tgSender.messageChannel.send(message)
        }
    }

    private fun filterContainerMessage(message: ContainerMessage): Boolean {
        // check if we have container specific filters
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null) {
            return !containerFilters.containerEvents.filter(message)
        }

        // if no filter then by global filters
        if (!containerNameFilter.filter(message))
            return true

        if (!containerEventFilter.filter(message))
            return true

        return false
    }

    private fun processLogMessage(message: LogMessage) {
        if (filterLogMessage(message))
            return

        logger.debug { "Message ${TgMessage.readableMessage(message).take(100)} accepted by all filters" }
        coroutineScope.launch {
            tgSender.messageChannel.send(message)
        }
    }

    private fun filterLogMessage(message: LogMessage): Boolean {
        // check if we have container specific filters
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null) {
            return !containerFilters.messageSubstring.filter(message)
        }

        // if no filter then by global filters
        if (!containerNameFilter.filter(message))
            return true

        if (!messageSubstringFilter.filter(message))
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
