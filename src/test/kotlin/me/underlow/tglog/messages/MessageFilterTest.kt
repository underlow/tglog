package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import LogsEvents
import kotlinx.coroutines.test.runTest
import me.underlow.tglog.tg.TgBotService
import me.underlow.tglog.tg.TgSender
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class MessageFilterTest {

    @Test
    fun `processContainerMessage should include message when it is in include list for concrete container independent of global settings`() = runTest {
        val messageReceiver = MessageReceiver()
        val logsEventConfiguration = LogsEventConfiguration()
        val containerEventsConfiguration = ContainerEventsConfiguration(include = "*")
        val containerNamesConfiguration = ContainerNamesConfiguration(include = "*")
        val containersProperties = ContainersProperties(
            listOf(
                ContainerProperties(
                    name = "name1",
                    logs = LogsEvents(LogsEventConfiguration(include = "event1"))
                )
            )
        )
        val tgBotService = mock<TgBotService>()
        val tgSender = TgSender(tgBotService)

        val messageFilter = MessageFilter(
            messageReceiver,
            logsEventConfiguration,
            containerEventsConfiguration,
            containerNamesConfiguration,
            containersProperties,
            tgSender
        )

        val message = LogMessage("name1", "event1")

        messageReceiver.receiveMessage(message)

        // assert that message is sent to tgSender
        verify(tgBotService, times(1)).sendMessage(any())

    }


}
