import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * all include/exclude properties are comma separated lists of values
 */
@ConfigurationProperties("log.events")
data class LogEventConfiguration(
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
