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
        logger.info { "Initializing Telegram Bot " +
                "with token ${configuration.botToken.take(4)}***** " +
                "for chat ${configuration.chatId.take(4)}*****" }

        bot = TelegramBot(configuration.botToken)
        logger.info { "Telegram Bot successfully initialized" }
    }

    fun sendMessage(fullMessage: String): Int {
        logger.debug { "Sending message: $fullMessage to chatId: ${configuration.chatId}" }
        // instead of implementing complex wait logic to prevent bot from getting Too Many Requests
        // we can just sleep for a while. nobody will need more than 1 message per second
        Thread.sleep(500)

        // nothing complicated here, let's do 3 retry attempts and then skip
        repeat(3) {
            try {
                val sendMessage = SendMessage(configuration.chatId, fullMessage)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)

                val sendResponse = bot.execute(sendMessage)

                if (!sendResponse.isOk) {
                    logger.info { "Error while sending message: ${sendResponse.description()} error: ${sendResponse.errorCode()}" }
                    return@repeat
                }

                return sendResponse.message().messageId()
            } catch (e: Exception) {
                logger.error(e) { "Error while sending message, retrying" }
                Thread.sleep(1000)
            }
        }

        // if we got here it means that sending message failed 3 times
        logger.error { "Error sending message: $fullMessage to chatId: ${configuration.chatId}" }
        // do not throw exception to avoid stopping the whole process
        return -1
    }
}

private val logger = KotlinLogging.logger { }
