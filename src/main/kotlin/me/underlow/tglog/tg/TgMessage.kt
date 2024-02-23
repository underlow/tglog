package me.underlow.tglog.tg

import me.underlow.tglog.messages.ContainerMessage
import me.underlow.tglog.messages.LogMessage
import me.underlow.tglog.messages.Message

object TgMessage {

    fun readableMessage(message: Message): String {
        return when (message) {
            is LogMessage -> logMessage(message)
            is ContainerMessage -> containerMessage(message)
        }

    }

    private fun logMessage(message: LogMessage) =
        "Container <b>${message.containerName}</b>\n\n ${message.message}"

    private fun containerMessage(message: ContainerMessage): String {
        val eventText = when (message.event) {
            "start" -> "started"
            "die" -> "died"
            else -> "has event ${message.event}" // cannot find docs, let's add later
        }
        return "Container <b>${message.containerName}</b> has event $eventText"
    }

}
