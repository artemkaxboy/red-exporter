package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class IssueStatusServiceTest {

    @Autowired
    lateinit var issueStatusesRepository: IssueStatusRepository

    @Autowired
    lateinit var issueStatusService: IssueStatusService

    val testStatuses = listOf(
        IssueStatus.make(name = "New"),
        IssueStatus.make(name = "In Progress"),
        IssueStatus.make(name = "Re-Opened"),
        IssueStatus.make(name = "Closed", isClosed = 1)
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
