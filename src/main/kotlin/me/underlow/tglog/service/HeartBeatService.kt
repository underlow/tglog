package me.underlow.tglog.service

import com.cronutils.descriptor.CronDescriptor
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.underlow.tglog.messages.HeartBeatMessage
import me.underlow.tglog.tg.TgSender
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


/**
 * this service send periodic heartbeats to tg.
 * scheduling can be adjusted in application.aml with `tglog.heartbeat.cron` property
 */
@Service
class HeartBeatService(
    private val tgSender: TgSender,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    @Value("\${tglog.heartbeat.cron}")
    private lateinit var cron: String

    private val coroutineScope = CoroutineScope(coroutineDispatcher)


    @PostConstruct
    fun init() {
        logger.info { "Starting heartbeat service with cron settings: ${cronDescription(cron)}" }
    }

    @Scheduled(cron = "\${tglog.heartbeat.cron}")
    fun sendHeartBeat() {
        logger.debug { "Sending heartbeat" }
        coroutineScope.launch {
            tgSender.messageChannel.send(HeartBeatMessage)
        }
    }

    /**
     * returns human readable description of cron expression
     */
    fun cronDescription(cronExpression: String): String {
        val cronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
        val cron = cronParser.parse(cronExpression)
        return CronDescriptor.instance().describe(cron)
    }
}



private val logger = KotlinLogging.logger { }
