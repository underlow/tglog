package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import LogEventConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.underlow.tglog.tg.TgBotService
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@Service
@EnableConfigurationProperties(
    LogEventConfiguration::class,
    ContainerEventsConfiguration::class,
    ContainerNamesConfiguration::class
)
class MessageProcessor(
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

    private fun processContainerMessage(message: ContainerMessage) {
        if (message.containerName in containerNamesConfiguration.exclude) {
            logger.debug { "Container ${message.containerName} is excluded from processing" }
            return
        }

        if (containerEventsConfiguration.include.isNotEmpty() &&
            message.message !in containerEventsConfiguration.include
        ) {
            logger.debug { "Container ${message.containerName} is not included in processing" }
            return
        }

        if (message.message in containerEventsConfiguration.exclude) {
            logger.debug { "Message ${message.message} is excluded from processing" }
            return
        }
        if (containerEventsConfiguration.include.isNotEmpty() &&
            message.message !in containerEventsConfiguration.include
        ) {
            logger.debug { "Message ${message.message} is not included in processing" }
            return
        }

        tgBot.sendMessage(message.containerName, message.message) // todo: change to more friendly messages
    }

    private fun processLogMessage(message: LogMessage) {
        if (message.containerName in containerNamesConfiguration.exclude) {
            logger.debug { "Container ${message.containerName} is excluded from processing" }
            return
        }

        if (containerEventsConfiguration.include.isNotEmpty() &&
            message.message !in containerEventsConfiguration.include
        ) {
            logger.debug { "Container ${message.containerName} is not included in processing" }
            return
        }

        if (logEventConfiguration.exclude.isNotEmpty() &&
            logEventConfiguration.exclude.split(",").all { it !in message.message }
        ) {
            logger.trace { "Message ${message.message} is excluded from processing" }
            return
        }
        if (containerEventsConfiguration.include.isNotEmpty() &&
            logEventConfiguration.include.split(",").any { it in message.message }
        ) {
            logger.trace { "Message ${message.message} is not included in processing" }
            return
        }

        tgBot.sendMessage(message.containerName, message.message) // todo: change to more friendly messages
    }
}

private val coroutineScope = CoroutineScope(Dispatchers.Default)

private val logger = KotlinLogging.logger { }
