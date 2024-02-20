package me.underlow.tglog.tg

import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * @param botToken token of tg bot
 *
 */
@ConfigurationProperties("tgbot")
data class TgBotConfiguration(
    val botToken: String,
    val chatId: String
)
