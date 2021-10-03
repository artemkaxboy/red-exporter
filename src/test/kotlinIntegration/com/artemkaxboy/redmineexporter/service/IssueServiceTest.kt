package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.getVersion
import org.assertj.core.api.Assertions
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.junit.jupiter.Testcontainers
import utils.MysqlContainer


@SpringBootTest
@RunWith(SpringRunner::class)
//@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
//@Testcontainers
internal class IssueServiceTest() {

    @ClassRule
    val mysqlSQLContainer = MysqlContainer.getInstance()

    @Autowired
    lateinit var issueService: IssueService

    @Test
    fun getMetricByVersionIdAndStatusId() {
        val version = getVersion()


        issueService.fetchMetrics(listOf(version))

        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, 1)).isEqualTo(0)
    }

    @Test
    fun fetchMetrics() {
    }
}
