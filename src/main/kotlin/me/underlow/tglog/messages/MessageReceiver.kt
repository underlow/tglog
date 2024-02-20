package me.underlow.tglog.messages

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class MessageReceiver {

    val queue = Channel<Message>()

    fun receiveMessage(message: Message) {
        println("Received message $message")
        coroutineScope.launch {
            queue.send(message)
        }
    }
}


private val coroutineScope = CoroutineScope(Dispatchers.Default)

sealed interface Message

data class LogMessage(val containerName: String, val message: String) : Message
data class ContainerMessage(val containerName: String, val message: String) : Message
