package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import LogEventConfiguration
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
    LogEventConfiguration::class,
    ContainerEventsConfiguration::class,
    ContainerNamesConfiguration::class
)
class MessageFilter(
    private val messageReceiver: MessageReceiver,
    private val logEventConfiguration: LogEventConfiguration,
    private val containerEventsConfiguration: ContainerEventsConfiguration,
    private val containerNamesConfiguration: ContainerNamesConfiguration,
    private val tgBot: TgBotService
) {

    init {
        coroutineScope.launch {
            for (message in messageReceiver.queue) {
                when (message) {
                    is LogMessage -> processLogMessage(message)
                    is ContainerMessage -> processContainerMessage(message)
                }
            }
        }
    }

    private val containerNameFilter = ContainerNameFilter(containerNamesConfiguration)
    private val containerEventFilter = ContainerEventFilter(containerEventsConfiguration)
    private val messageSubstringFilter = MessageSubstringFilter(logEventConfiguration)

    private fun processContainerMessage(message: ContainerMessage) {
        if (!containerNameFilter.filter(message))
            return

        if (!containerEventFilter.filter(message))
            return

        logger.debug { "Sending message ${TgMessage.readableMessage(message)} " }
        tgBot.sendMessage(TgMessage.readableMessage(message))
    }

    private fun processLogMessage(message: LogMessage) {
        if (!containerNameFilter.filter(message))
            return

        if (!messageSubstringFilter.filter(message))
            return

        logger.debug { "Sending message ${TgMessage.readableMessage(message)} " }
        tgBot.sendMessage(TgMessage.readableMessage(message))
    }
}

private val coroutineScope = CoroutineScope(Dispatchers.Default)

private val logger = KotlinLogging.logger { }
