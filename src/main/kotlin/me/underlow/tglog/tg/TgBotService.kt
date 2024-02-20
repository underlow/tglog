package me.underlow.tglog.tg

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.request.SendPhoto
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@EnableConfigurationProperties(TgBotConfiguration::class)
@Component
class TgBotService(
    private val configuration: TgBotConfiguration,
) {
    private final val bot: TelegramBot

    init {
        logger.info { "Initializing Telegram Bot with token ${configuration.botToken.take(4)}***** for chat ${configuration.chatId.take(4)}*****" }
        bot = TelegramBot(configuration.botToken)
        logger.info { "Telegram Bot successfully initialized" }
    }

    fun sendMessage(containerName: String, fullMessage: String): Int {
        val sendMessage = SendMessage(configuration.chatId, "<b>$containerName</b>\n $fullMessage")
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)

        val sendResponse = bot.execute(sendMessage)

        if (!sendResponse.isOk) {
            //todo: retries
            logger.error { "Error while sending message: ${sendResponse.description()}" }
            throw Exception("Error while sending message: ${sendResponse.description()}")
        }

        return sendResponse.message().messageId()
    }
}

private val logger = KotlinLogging.logger { }