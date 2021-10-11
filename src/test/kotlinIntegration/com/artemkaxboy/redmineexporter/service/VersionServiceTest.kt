package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.Project
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.metrics.StatusMetricsRegistry
import com.artemkaxboy.redmineexporter.repository.ProjectRepository
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VersionServiceTest {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var versionRepository: VersionRepository

    @Autowired
    lateinit var versionService: VersionService

    @MockkBean
    lateinit var redmineProperties: RedmineProperties

    @MockkBean
    lateinit var statusMetricsRegistry: StatusMetricsRegistry

    lateinit var project: Project

    lateinit var testVersions: List<Version>

    @BeforeAll
    fun initDb() {
        project = projectRepository.save(Project.make(name = "Main Project"))
        testVersions = listOf(
            Version.make(name = "v0.1", projectId = project.id),
            Version.make(name = "v0.2", projectId = project.id),
            Version.make(name = "v0.2.1", projectId = project.id),
            Version.make(name = "v1.0", projectId = project.id)
        )
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
    fun `calls versionOpened on fetching versions`() {

        every { redmineProperties.projects } returns listOf(project.id)
        justRun { statusMetricsRegistry.versionOpened(any()) }

        versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()
        verify(exactly = testVersions.size) { statusMetricsRegistry.versionOpened(any()) }
    }

    @Test
    fun `returns all versions after fetch`() {

        every { redmineProperties.projects } returns listOf(project.id)
        justRun { statusMetricsRegistry.versionOpened(any()) }

        val expected = versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()

        val got = versionService.getAllVersions()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `clears all statuses by reset`() {

        every { redmineProperties.projects } returns listOf(project.id)
        justRun { statusMetricsRegistry.versionOpened(any()) }

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
