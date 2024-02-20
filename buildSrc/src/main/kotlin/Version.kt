import org.gradle.api.JavaVersion

object Kotlin {
    const val version = "1.8.10"
    val javaSource = JavaVersion.VERSION_17
    val javaTarget = JavaVersion.VERSION_17
}

object Versions {
    const val springBoot = "3.2.2"
    const val springBootDependency = "1.1.4"

    const val testContainers = "1.19.5"

    const val kotlinLogging = "2.1.23"
    const val kotest = "5.4.1"
    const val jupiter = "5.9.0"
}
