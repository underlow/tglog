package me.underlow.tglog

import jakarta.annotation.PostConstruct
import org.jeasy.random.EasyRandom
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(
    classes = [TgLogService::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.config.location=classpath:application-test.yml"]
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class BaseTest {

    protected val easyRandom = EasyRandom()

}

inline fun <reified T> EasyRandom.random(): T = this.nextObject(T::class.java)
fun  EasyRandom.randomString(length: Int): String = buildString{
    for (i in 0 until length) {
        append(this@randomString.random<Char>())
    }
}
