package me.underlow.tglog.messages

import ContainerEventsConfiguration
import ContainerNamesConfiguration
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ContainerNameFilterTest {

    @ParameterizedTest(name = "ContainerNameFilterTest - with {0} - returns correctly filtered name")
    @EnumSource(ContainerNameFilterTestParams::class)
    fun `filter returns true when container name not in exclude list and include list empty`(
        params: ContainerNameFilterTestParams
    ) {
        // Given
        val filter = ContainerNameFilter(params.configuration)

        // When
        val result = filter.filter(params.message)

        // Then
        assertTrue(result == params.result)
    }
}

enum class ContainerNameFilterTestParams(
    val configuration: ContainerNamesConfiguration,
    val message: Message,
    val result: Boolean
) {
    `if container name is in exclude list, message is excluded`(
        ContainerNamesConfiguration(exclude = "name1"),
        ContainerMessage("name1", "event1"),
        false
    ),

    `if include list is empty then message is included (no filtering for empty include list)`(
        ContainerNamesConfiguration(),
        ContainerMessage("name1","event1"),
        true
    ),

    `if include list is not empty and name is not in include list, message is excluded`(
        ContainerNamesConfiguration(include = "name2"),
        ContainerMessage("name1","event1"),
        false
    ),
    `if include list is not empty and name is in include list, message is included`(
        ContainerNamesConfiguration(include = "name1", exclude = "name2"),
        ContainerMessage("name1","event1"),
        true
    ),
    `if container name is in exclude list and include list is not empty but does not contain name, message is excluded`(
        ContainerNamesConfiguration(include = "name2", exclude = "name1"),
        ContainerMessage("name1","event1"),
        false
    ),

    `if container name is in exclude list and include list is not empty and contains name, message is excluded`(
        ContainerNamesConfiguration(include = "name1", exclude = "name1"),
        ContainerMessage("name1","event1"),
        false
    ),

    `if container name is not in exclude list and include list is empty, message is included`(
        ContainerNamesConfiguration(include = "", exclude = "name2"),
        ContainerMessage("name1","event1"),
        true
    ),
}
