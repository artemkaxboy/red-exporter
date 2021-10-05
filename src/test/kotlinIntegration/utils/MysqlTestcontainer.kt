package utils

import org.testcontainers.containers.MySQLContainer

const val MYSQL_IMAGE = "mysql:8"
const val DEFAULT_MYSQL_PORT = 3306

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
