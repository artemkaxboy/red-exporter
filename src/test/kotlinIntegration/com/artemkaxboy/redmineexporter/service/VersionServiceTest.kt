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
    fun `notifies registry on fetching new versions`() {

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
    fun `notifies registry on closing one versions`() {

        every { redmineProperties.projects } returns listOf(project.id)
        justRun { statusMetricsRegistry.versionOpened(any()) }
        justRun { statusMetricsRegistry.versionClosed(any()) }

        val saved = versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()

        versionRepository.delete(saved.first())
        versionService.fetchVersionsForPreconfiguredProjects()
        verify(exactly = 1) { statusMetricsRegistry.versionClosed(any()) }
    }

    @Test //@Disabled("Must fix") // TODO closing all versions of project doesn't call version closed
    fun `notifies registry on closing all versions`() {

        every { redmineProperties.projects } returns listOf(project.id)
        justRun { statusMetricsRegistry.versionOpened(any()) }
        justRun { statusMetricsRegistry.versionClosed(any()) }

        val saved = versionRepository.saveAll(testVersions)
        versionService.fetchVersionsForPreconfiguredProjects()

        versionRepository.deleteAllById(saved.map { it.id })
        versionService.fetchVersionsForPreconfiguredProjects()
        verify(exactly = 4) { statusMetricsRegistry.versionClosed(any()) }
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
}
