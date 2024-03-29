package me.underlow.tglog.messages

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class MessageQueue(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    val messageChannel = Channel<Message>()

    fun putMessage(message: Message) {
        logger.trace { "Received message $message" }
        coroutineScope.launch {
            messageChannel.send(message)
        }
    }
}


sealed interface Message {
    val containerName: String
}

data class LogMessage(override val containerName: String, val message: String) : Message
data class ContainerMessage(override val containerName: String, val event: String) : Message

object HeartBeatMessage : Message {
    override val containerName: String = ""
}

private val logger = KotlinLogging.logger { }
