package com.artemkaxboy.redmineexporter

import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import utils.MysqlContainer

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
class RedmineExporterApplicationTests {

    @ClassRule
    val mysqlSQLContainer = MysqlContainer.instance

    @Test
    fun contextLoads() {
    }
}
