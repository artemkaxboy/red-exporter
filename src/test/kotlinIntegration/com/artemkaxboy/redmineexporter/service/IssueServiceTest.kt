package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.*
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import com.artemkaxboy.redmineexporter.repository.ProjectRepository
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import org.assertj.core.api.Assertions
import org.junit.ClassRule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import utils.MysqlContainer


@SpringBootTest
@RunWith(SpringRunner::class)
@TestPropertySource(locations = ["classpath:application-integrationtest.properties"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IssueServiceTest {

    @ClassRule
    val mysqlSQLContainer = MysqlContainer.instance

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var versionRepository: VersionRepository

    @Autowired
    lateinit var issueRepository: IssueRepository

    @Autowired
    lateinit var issueService: IssueService

    lateinit var project: Project
    lateinit var version: Version

    @BeforeAll
    fun initDb() {
        project = projectRepository.save(makeProject(id = -1, name = "Main Project"))
        version = versionRepository.save(makeVersion(id = -1, name = "Wishlist", projectId = project.id))
    }

    @AfterEach
    fun clearIssues() {
        issueRepository.deleteAllInBatch()
        issueService.reset()
    }

    @Test
    fun getMetricByVersionIdAndStatusId() {
        val statusId1 = 101L
        val count1 = 5L

        val statusId2 = 102L
        val count2 = 6L

        generateIssues(statusId1, count1)
        generateIssues(statusId2, count2)

        issueService.fetchMetrics(listOf(version))

        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId2)).isEqualTo(count2)
    }

    @Test
    fun `returns 0 metrics without fetch`() {
        val statusId1 = 101L
        val count1 = 5L

        generateIssues(statusId1, count1)

        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(0)
    }

    @Test
    fun `returns correct metrics after fetch`() {
        val statusId1 = 101L
        val count1 = 5L

        generateIssues(statusId1, count1)
        issueService.fetchMetrics(listOf(version))

        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
    }

    @Test
    fun `returns old metrics value before fetch`() {
        val statusId1 = 101L
        val count1 = 5L

        generateIssues(statusId1, count1)
        issueService.fetchMetrics(listOf(version))
        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)

        generateIssues(statusId1, count1)
        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
    }

    @Test
    fun `returns correct metrics after update and fetch`() {
        val statusId1 = 101L
        val count1 = 5L

        generateIssues(statusId1, count1)
        issueService.fetchMetrics(listOf(version))
        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)

        generateIssues(statusId1, count1)
        issueService.fetchMetrics(listOf(version))
        Assertions.assertThat(issueService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1 * 2)
    }


    fun generateIssues(statusId: Long, count: Long) {
        (1..count)
            .map {
                makeIssue(
                    id = -1,
                    projectId = project.id,
                    fixedVersionId = version.id,
                    statusId = statusId
                )
            }
            .also { issueRepository.saveAll(it) }

    }
}
