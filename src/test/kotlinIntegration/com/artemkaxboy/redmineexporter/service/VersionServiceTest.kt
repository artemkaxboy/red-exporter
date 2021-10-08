package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Project
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.repository.ProjectRepository
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

const val TEST_PROJECT_ID = 17L

@SpringBootTest(properties = ["redmine.projects=${TEST_PROJECT_ID}"])
@RunWith(SpringRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VersionServiceTest {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var versionRepository: VersionRepository

    @Autowired
    lateinit var versionService: VersionService

    lateinit var project: Project

    val testVersions = listOf(
        Version.make(id = 1, name = "v0.1", projectId = TEST_PROJECT_ID),
        Version.make(id = 2, name = "v0.2", projectId = TEST_PROJECT_ID),
        Version.make(id = 3, name = "v0.2.1", projectId = TEST_PROJECT_ID),
        Version.make(id = 4, name = "v1.0", projectId = TEST_PROJECT_ID)
    )

    @BeforeAll
    fun initDb() {
        project = projectRepository.save(Project.make(id = TEST_PROJECT_ID, name = "Main Project"))
    }

    @AfterAll
    fun cleanDb() {
        projectRepository.deleteAllInBatch()
    }

    @AfterEach
    fun cleanVersions() {
        versionRepository.deleteAllInBatch()
        versionService.reset()
    }

    @Test
    fun `returns no versions before fetch`() {

        versionRepository.saveAll(testVersions)

        val got = versionService.getAllVersions()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all statuses after fetch`() {

        val expected = versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()

        val got = versionService.getAllVersions()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `clears all statuses by reset`() {

        versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()
        versionService.reset()

        val got = versionService.getAllVersions()
        Assertions.assertThat(got).isEmpty()
    }

// TODO
//    @Test
//    fun `returns 0 metrics for unknown statusId`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        val unknownStatusId = 102L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, unknownStatusId)).isEqualTo(0)
//    }
//
//    @Test
//    fun `returns metrics for only one statusId`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
//    }
//
//    @Test
//    fun `returns metrics for each of many statusId`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        val statusId2 = statusId1 + 1
//        val count2 = count1 + 1
//
//        val statusId3 = statusId2 + 1
//        val count3 = count2 + 1
//
//        generateIssues(statusId1, count1)
//        generateIssues(statusId2, count2)
//        generateIssues(statusId3, count3)
//        versionService.fetchMetrics(listOf(version))
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId2)).isEqualTo(count2)
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId3)).isEqualTo(count3)
//    }
//
//    @Test
//    fun `returns 0 metrics without fetch`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(0)
//    }
//
//    @Test
//    fun `returns correct metrics after fetch`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
//    }
//
//    @Test
//    fun `returns old metrics value before fetch`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//        generateIssues(statusId1, count1)
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1)
//    }
//
//    @Test
//    fun `returns correct metrics after update and fetch`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(count1 * 2)
//    }
//
//    @Test
//    fun `reset actually resets metrics`() {
//        val statusId1 = 101L
//        val count1 = 5L
//
//        generateIssues(statusId1, count1)
//        versionService.fetchMetrics(listOf(version))
//        versionService.reset()
//
//        Assertions.assertThat(versionService.getMetricByVersionIdAndStatusId(version.id, statusId1)).isEqualTo(0)
//    }
//
//    fun generateIssues(statusId: Long, count: Long) {
//        (1..count)
//            .map {
//                Issue.make(
//                    id = -1,
//                    projectId = project.id,
//                    fixedVersionId = version.id,
//                    statusId = statusId
//                )
//            }
//            .also { ver.saveAll(it) }
//
//    }
}
