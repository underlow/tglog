package me.underlow.tglog.tg

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import me.underlow.tglog.messages.Message
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TgSender(
    private val tgBotService: TgBotService,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    val messageChannel = Channel<Message>()

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    init {
        coroutineScope.launch {
            for (message in messageChannel) {
                logger.trace { "Send message to tg: $message" }
                tgBotService.sendMessage(TgMessage.readableMessage(message))
                // current tg limit is 20 messages per minute
                delay(3000)
            }
        }

    }
}

private val logger = KotlinLogging.logger { }
