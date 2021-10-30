package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueTracker
import com.artemkaxboy.redmineexporter.repository.IssueTrackerRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class IssueTrackerServiceTest {

    @Autowired
    lateinit var issueTrackerRepository: IssueTrackerRepository

    @Autowired
    lateinit var issueTrackerService: IssueTrackerService

    val testStatuses = listOf(
        IssueTracker.make(name = "Feature"),
        IssueTracker.make(name = "Bug"),
        IssueTracker.make(name = "Question"),
    )

    @AfterEach
    fun clearIssues() {
        issueTrackerRepository.deleteAllInBatch()
        issueTrackerService.reset()
    }

    @Test
    fun `returns no trackers before fetch`() {

        issueTrackerRepository.saveAll(testStatuses)

        val got = issueTrackerService.getAllTrackers()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all trackers after fetch`() {

        val expected = issueTrackerRepository.saveAll(testStatuses)
        issueTrackerService.fetchTrackers()

        val got = issueTrackerService.getAllTrackers()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `clears all trackers by reset`() {

        issueTrackerRepository.saveAll(testStatuses)
        issueTrackerService.fetchTrackers()
        issueTrackerService.reset()

        val got = issueTrackerService.getAllTrackers()
        Assertions.assertThat(got).isEmpty()
    }
}
