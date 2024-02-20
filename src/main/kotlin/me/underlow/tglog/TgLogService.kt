package me.underlow.tglog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TgLogService

fun main(args: Array<String>) {
    runApplication<TgLogService>(*args)
}

