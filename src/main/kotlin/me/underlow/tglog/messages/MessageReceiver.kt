package me.underlow.tglog.messages

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class MessageReceiver {

    val messageChannel = Channel<Message>()

    fun receiveMessage(message: Message) {
        logger.trace {"Received message $message"}
        coroutineScope.launch {
            messageChannel.send(message)
        }
    }
}


private val coroutineScope = CoroutineScope(Dispatchers.Default)

sealed interface Message{
    val containerName: String
}

data class LogMessage(override val containerName: String, val message: String) : Message
data class ContainerMessage(override val containerName: String, val event: String) : Message

private val logger = KotlinLogging.logger { }
