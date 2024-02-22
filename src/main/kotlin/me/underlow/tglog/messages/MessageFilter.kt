package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.underlow.tglog.tg.TgBotService
import me.underlow.tglog.tg.TgMessage
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
    private val messageReceiver: MessageReceiver,
    private val logsEventConfiguration: LogsEventConfiguration,
    private val containerEventsConfiguration: ContainerEventsConfiguration,
    private val containerNamesConfiguration: ContainerNamesConfiguration,
    private val containersProperties: ContainersProperties,
    private val tgBot: TgBotService
) {

    init {
        logger.debug { "Starting message filter" }
        coroutineScope.launch {
            for (message in messageReceiver.messageChannel) {
                when (message) {
                    is LogMessage -> processLogMessage(message)
                    is ContainerMessage -> processContainerMessage(message)
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
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null && !containerFilters.containerEvents.filter(message))
            return

        if (!containerNameFilter.filter(message))
            return

        if (!containerEventFilter.filter(message))
            return

        logger.debug { "Sending message ${TgMessage.readableMessage(message)} " }
        tgBot.sendMessage(TgMessage.readableMessage(message))
    }

    private fun processLogMessage(message: LogMessage) {
        val containerFilters = containerFilters[message.containerName]
        if (containerFilters != null && !containerFilters.messageSubstring.filter(message))
            return

        if (!containerNameFilter.filter(message))
            return

        if (!messageSubstringFilter.filter(message))
            return

        logger.debug { "Sending message ${TgMessage.readableMessage(message)} " }
        tgBot.sendMessage(TgMessage.readableMessage(message))
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

private val coroutineScope = CoroutineScope(Dispatchers.Default)

private val logger = KotlinLogging.logger { }
