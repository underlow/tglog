package me.underlow.tglog.messages

import ContainerEvents
import ContainerEventsConfiguration
import ContainerNamesConfiguration
import ContainerProperties
import ContainersProperties
import LogsEventConfiguration
import LogsEvents
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MessageFilterTest {

    @Test
    fun `processLogMessage should include message when it is in include list for concrete container independent of global settings`() {
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
        val messageFilter = MessageFilter(
            logsEventConfiguration,
            containerEventsConfiguration,
            containerNamesConfiguration,
            containersProperties,
        )

        val message = LogMessage("name1", "event1")

        "Message should pass filter".asClue {
            messageFilter.filterLogMessage(message) shouldBe true
        }

    }

    @Test
    fun `processContainerMessage should include message when it is in include list for concrete container independent of global settings`() {
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

        val messageFilter = MessageFilter(
            logsEventConfiguration,
            containerEventsConfiguration,
            containerNamesConfiguration,
            containersProperties,
        )

        val message = ContainerMessage("name1", "event1")
        "Message should pass filter".asClue {
            messageFilter.filterContainerMessage(message) shouldBe true
        }

    }
}
