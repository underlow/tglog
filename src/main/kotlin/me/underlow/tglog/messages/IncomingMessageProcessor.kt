package me.underlow.tglog.messages

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.underlow.tglog.tg.TgMessage
import me.underlow.tglog.tg.TgSender
import mu.KotlinLogging
import org.springframework.stereotype.Service


/**
 * This class is responsible for getting messages from messageQueue and filtering them with [MessageFilter] and then senging to [TgSender]
 */
@Service
class IncomingMessageProcessor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val tgSender: TgSender,
    private val messageQueue: MessageQueue,
    private val messageFilter: MessageFilter,
) {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    init {
        logger.debug { "Starting message processor" }
        // after start send message containing all filtering setting
        coroutineScope.launch {
            tgSender.messageChannel.send(
                LogMessage(
                    "tglog",
                    "<b>Filters</b>: \n${messageFilter.humanReadableFilters()}"
                )
            )
        }


        coroutineScope.launch {
            for (message in messageQueue.messageChannel) {
                filterAndSend(message)
            }
        }
    }

    private fun filterAndSend(message: Message) {
        if (doFilter(message)) {
            logger.debug { "Message ${TgMessage.readableMessage(message).take(100)} accepted by all filters" }
            coroutineScope.launch {
                tgSender.messageChannel.send(message)
            }
        }
    }

    private fun doFilter(message: Message): Boolean {
        val filteringResult = when (message) {
            is LogMessage -> messageFilter.filterLogMessage(message)
            is ContainerMessage -> messageFilter.filterContainerMessage(message)
            is HeartBeatMessage -> false // should never really be here, heartbeat message couldn't be received from logs
        }
        return filteringResult
    }
}

private val logger = KotlinLogging.logger { }
