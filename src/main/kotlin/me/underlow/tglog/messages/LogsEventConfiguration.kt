import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * all include/exclude properties are comma separated lists of values
 */
@ConfigurationProperties("logs.events")
data class LogsEventConfiguration(
    val include: String = "",
    val exclude: String = ""
)

@ConfigurationProperties("container.events")
data class ContainerEventsConfiguration(
    val include: String = "",
    val exclude: String = ""
)

@ConfigurationProperties("container.names")
data class ContainerNamesConfiguration(
    val include: String = "",
    val exclude: String = ""
)

@ConfigurationProperties("containers")
data class ContainersProperties(
    val container: List<ContainerProperties>
)

data class ContainerProperties(
    val name: String,
    val logs: LogsEvents,
    val container: ContainerEvents
)

data class ContainerEvents (
    val events: ContainerEventsConfiguration
)

data class LogsEvents(
    val events: LogsEventConfiguration
)
