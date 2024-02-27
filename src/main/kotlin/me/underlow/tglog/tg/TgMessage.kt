package me.underlow.tglog.tg

import me.underlow.tglog.messages.ContainerMessage
import me.underlow.tglog.messages.HeartBeatMessage
import me.underlow.tglog.messages.LogMessage
import me.underlow.tglog.messages.Message
import java.time.Instant

object TgMessage {

    fun readableMessage(message: Message): String {
        return when (message) {
            is LogMessage -> logMessage(message)
            is ContainerMessage -> containerMessage(message)
            is HeartBeatMessage -> heartBeatMessage()
        }

    }

    private fun heartBeatMessage(): String = "TgLog is still alive and well"

    private fun logMessage(message: LogMessage) =
        "Container <b>${message.containerName}</b>\n\n ${message.message}"

    private fun containerMessage(message: ContainerMessage): String {
        val eventText = when (message.event) {
            "start" -> "started"
            "die" -> "died"
            else -> message.event // cannot find docs, let's add later
        }
        return "Container <b>${message.containerName}</b> has event $eventText"
    }

}
