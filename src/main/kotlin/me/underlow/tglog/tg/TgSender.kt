package me.underlow.tglog.tg

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.underlow.tglog.messages.Message
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TgSender(
    private val tgBotService: TgBotService
) {
    val messageChannel = Channel<Message>()

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

private val coroutineScope = CoroutineScope(Dispatchers.Default)
private val logger = KotlinLogging.logger { }
