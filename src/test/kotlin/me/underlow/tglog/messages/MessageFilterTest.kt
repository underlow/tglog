package me.underlow.tglog.messages

import ContainerEvents
import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import LogsEvents
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.underlow.tglog.tg.TgBotService
import me.underlow.tglog.tg.TgSender
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class MessageFilterTest {

    @Test
    fun `processLogMessage should include message when it is in include list for concrete container independent of global settings`() = runTest {
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
            tgSender,
//            UnconfinedTestDispatcher()
        )

        val message = LogMessage("name1", "event1")

        messageReceiver.receiveMessage(message)

        // assert that message is sent to tgSender
        verify(tgBotService, times(1)).sendMessage(any())

    }

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
                    container = ContainerEvents(ContainerEventsConfiguration(include = "event1"))
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
            tgSender,
//            UnconfinedTestDispatcher()
        )

        val message = ContainerMessage("name1", "event1")

        messageReceiver.receiveMessage(message)

        // assert that message is sent to tgSender
        verify(tgBotService, times(1)).sendMessage(any())
    }
}
