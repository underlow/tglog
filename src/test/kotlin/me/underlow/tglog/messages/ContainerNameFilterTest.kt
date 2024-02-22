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

    `if include list is empty then message is excluded`(
        ContainerNamesConfiguration(),
        ContainerMessage("name1","event1"),
        false
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

    `if container name is not in exclude list and include list is empty, message is excluded`(
        ContainerNamesConfiguration(include = "", exclude = "name2"),
        ContainerMessage("name1","event1"),
        false
    ),
    `if container name is in exclude list and in include list, message is excluded`(
        ContainerNamesConfiguration(exclude = "container1", include = "container1"),
        ContainerMessage("container1", "event2"),
        false
    ),
    `if container name is not in exclude list and include list is star, message is included`(
        ContainerNamesConfiguration(include = "*", exclude = "container2"),
        ContainerMessage("container1","event1"),
        true
    ),
    `if container name is in exclude list and include list is star, message is excluded`(
        ContainerNamesConfiguration(include = "*", exclude = "container1"),
        ContainerMessage("container1","event1"),
        false
    ),
    `container name should match  exclude list when name is in uppercase and list is in lowercase`(
        ContainerNamesConfiguration(include = "*", exclude = "container1"),
        ContainerMessage("CONTAINER1","event1"),
        false
    ),
    `container name should match  exclude list when name is in lowercase and list is in uppercase`(
        ContainerNamesConfiguration(include = "*", exclude = "CONTAINER1"),
        ContainerMessage("container1","event1"),
        false
    ),
}
