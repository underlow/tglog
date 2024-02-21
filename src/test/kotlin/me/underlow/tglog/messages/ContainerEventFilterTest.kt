package me.underlow.tglog.messages

import ContainerEventsConfiguration
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ContainerEventFilterTest {

    @ParameterizedTest(name = "ContainerEventFilterTest - with {0} - returns correctly filtered event")
    @EnumSource(ContainerEventFilterTestParams::class)
    fun `filter returns true when message event not in exclude list and include list empty`(
        params: ContainerEventFilterTestParams
    ) {
        // Given
        val filter = ContainerEventFilter(params.configuration)

        // When
        val result = filter.filter(params.message)

        // Then
        assertTrue(result == params.result)
    }
}

enum class ContainerEventFilterTestParams(
    val configuration: ContainerEventsConfiguration,
    val message: ContainerMessage,
    val result: Boolean
) {
    `if container event is in exclude list, message is excluded`(
        ContainerEventsConfiguration(exclude = "event1"),
        ContainerMessage("container1", "event1"),
        false
    ),

    `if include list is empty then message is included (no filtering for empty include list)`(
        ContainerEventsConfiguration(),
        ContainerMessage("container1", "event1"),
        true
    ),
    `if include list is not empty and event is not in include list, message is excluded`(
        ContainerEventsConfiguration(include = "event2"),
        ContainerMessage("container1", "event1"),
        false
    ),

    `if include list is not empty and event is in include list, message is included`(
        ContainerEventsConfiguration(include = "event1"),
        ContainerMessage("container1", "event1"),
        true
    ),
    `if container event is in exclude list and include list is not empty but does not contain event, message is excluded`(
        ContainerEventsConfiguration(exclude = "event1", include = "event2"),
        ContainerMessage("container1", "event1"),
        false
    ),

    `if container event is in exclude list and include list is not empty and contains event, message is excluded`(
        ContainerEventsConfiguration(exclude = "event1", include = "event1"),
        ContainerMessage("container1", "event1"),
        false
    ),

    `if container event is not in exclude list and include list is empty, message is included`(
        ContainerEventsConfiguration(exclude = "event2"),
        ContainerMessage("container1", "event1"),
        true
    ),
}



