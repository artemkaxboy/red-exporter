package utils

import com.artemkaxboy.redmineexporter.service.BaeldungPostgresqlContainer
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait

const val MYSQL_IMAGE = "mysql:8"
const val DEFAULT_MYSQL_PORT = 3306

@ContextConfiguration(initializers = [AbstractContainerStarter.Initializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractContainerStarter {

    companion object {
        var dbContainer: MySQLContainer<Nothing> = object : MySQLContainer<Nothing>(MYSQL_IMAGE) {
            init {
                withExposedPorts(DEFAULT_MYSQL_PORT)
                withDatabaseName("database")
                withEnv("MYSQL_ROOT_PASSWORD", "root")
                withEnv("MYSQL_USER", "user")
                withEnv("MYSQL_PASSWORD", "password")
                withUsername("user")
                withPassword("password")
                waitingFor(
                    Wait.forLogMessage(".*mysqld: ready for connections.*", 1)
                )
            }
        }

        init {
            dbContainer.start()
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

            TestPropertyValues.of(
                "spring.datasource.url=jdbc:mysql://${dbContainer.containerIpAddress}:" +
                        "${dbContainer.getMappedPort(DEFAULT_MYSQL_PORT)}/${dbContainer.databaseName}",
                "spring.datasource.username=${dbContainer.username}",
                "spring.datasource.password=${dbContainer.password}",
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}

class MysqlContainer private constructor() : MySQLContainer<MysqlContainer>(MYSQL_IMAGE) {

    override fun start() {
        super.start()
        System.setProperty("DATABASE_HOST", container.containerIpAddress)
        System.setProperty("DATABASE_PORT", container.getMappedPort(DEFAULT_MYSQL_PORT).toString())
        System.setProperty("DATABASE_NAME", container.databaseName)
        System.setProperty("DATABASE_USERNAME", container.username)
        System.setProperty("DATABASE_PASSWORD", container.password)
    }

    companion object {
        private lateinit var container: MysqlContainer

        val instance: MysqlContainer
            get() {
                if (!::container.isInitialized) {
                    container = MysqlContainer()
                    container.start()
                }
                return container
            }
    }
}
