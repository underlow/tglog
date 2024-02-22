package me.underlow.tglog.messages

import LogEventConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class MessageSubstringFilterTest {

    @ParameterizedTest(name = "MessageSubstringFilterTest - with {0} - returns correctly filtered message")
    @EnumSource(MessageSubstringFilterTestParams::class)
    fun `filter returns true when message substring not in exclude list and include list empty`(
        params: MessageSubstringFilterTestParams
    ) {
        // Given
        val filter = MessageSubstringFilter(params.configuration)

        // When
        val result = filter.filter(params.message)

        // Then
        assertTrue(result == params.result)
    }
}


enum class MessageSubstringFilterTestParams(
    val configuration: LogEventConfiguration,
    val message: LogMessage,
    val result: Boolean
) {
    `if message substring is in exclude list, message is excluded`(
        LogEventConfiguration(exclude = "substring1"),
        LogMessage("name1", "event1 contains substring1"),
        false
    ),

    `if include list is empty then message is excluded`(
        LogEventConfiguration(),
        LogMessage("name1","event1"),
        false
    ),

    `if include list is not empty and message does not contain substring in include list, message is excluded`(
        LogEventConfiguration(include = "substring2"),
        LogMessage("name1","event1"),
        false
    ),

    `if include list is not empty and message contains substring in include list, message is included`(
        LogEventConfiguration(include = "substring1", exclude = "substring2"),
        LogMessage("name1","event1 contains substring1"),
        true
    ),

    `if message substring is in exclude list and include list is not empty but does not contain substring, message is excluded`(
        LogEventConfiguration(include = "substring2", exclude = "substring1"),
        LogMessage("name1","event1 contains substring1"),
        false
    ),

    `if message substring is in exclude list and include list is not empty and contains substring, message is excluded`(
        LogEventConfiguration(include = "substring1", exclude = "substring1"),
        LogMessage("name1","event1 contains substring1"),
        false
    ),

    `if message substring is not in exclude list and include list is empty, message is excluded`(
        LogEventConfiguration(include = "", exclude = "substring2"),
        LogMessage("name1","event1"),
        false
    ),
    `if message substring is in exclude list and in include list, message is excluded`(
        LogEventConfiguration(include = "substring2", exclude = "substring2"),
        LogMessage("name1","event1 contains substring2"),
        false
    ),
    `if message substring is not in exclude list and include list is star, message is included`(
        LogEventConfiguration(include = "*", exclude = "substring1"),
        LogMessage("name1","event1 contains substring2"),
        true
    ),
    `if message substring is in exclude list and include list is star, message is excluded`(
        LogEventConfiguration(include = "*", exclude = "substring2"),
        LogMessage("name1","event1 contains substring2"),
        false
    ),
    `message substring should match exclude list even if it in different cases`(
        LogEventConfiguration(include = "*", exclude = "SUBSTRING2"),
        LogMessage("name1","event1 contains substring2"),
        false
    ),
    `message substring should match include list even if it in different cases`(
        LogEventConfiguration(include = "substring2"),
        LogMessage("name1","EVENT1 CONTAINS SUBSTRING2"),
        true
    ),
}
