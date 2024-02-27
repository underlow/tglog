package me.underlow.tglog

import me.underlow.tglog.messages.MessageFilter
import me.underlow.tglog.service.HeartBeatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TgLogService {

    /**
     * [MessageFilter] bean do not have any references in the code and wouldn't be instantiated by Spring
     */
    @Autowired
    private lateinit var messageFilter: MessageFilter

    @Autowired
    private lateinit var heartbeatService: HeartBeatService
}


fun main(args: Array<String>) {
    runApplication<TgLogService>(*args)
}

