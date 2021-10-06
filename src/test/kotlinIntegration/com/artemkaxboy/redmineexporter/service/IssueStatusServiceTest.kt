package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.*
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.assertj.core.api.Assertions
import org.junit.ClassRule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import utils.MysqlContainer


@SpringBootTest
@RunWith(SpringRunner::class)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
internal class IssueStatusServiceTest {

    @ClassRule
    val mysqlSQLContainer = MysqlContainer.instance

    @Autowired
    lateinit var issueStatusesRepository: IssueStatusRepository

    @Autowired
    lateinit var issueStatusService: IssueStatusService

    val testStatuses = listOf(
        makeIssueStatus(id = 1, name = "New"),
        makeIssueStatus(id = 2, name = "In Progress"),
        makeIssueStatus(id = 3, name = "Re-Opened"),
        makeIssueStatus(id = 4, name = "Closed", isClosed = 1)
    )

    @AfterEach
    fun clearIssues() {
        issueStatusesRepository.deleteAllInBatch()
        issueStatusService.reset()
    }

    @Test
    fun `returns no statuses before fetch`() {

        issueStatusesRepository.saveAll(testStatuses)

        val got = issueStatusService.getAllStatuses()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all statuses after fetch`() {

        val expected = issueStatusesRepository.saveAll(testStatuses)
        issueStatusService.fetchStatuses()

        val got = issueStatusService.getAllStatuses()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `clears all statuses by reset`() {

        issueStatusesRepository.saveAll(testStatuses)
        issueStatusService.fetchStatuses()
        issueStatusService.reset()

        val got = issueStatusService.getAllStatuses()
        Assertions.assertThat(got).isEmpty()
    }
}
