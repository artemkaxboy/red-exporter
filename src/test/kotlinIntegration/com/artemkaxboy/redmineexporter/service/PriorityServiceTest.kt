package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.ACTIVE_IS_INACTIVE
import com.artemkaxboy.redmineexporter.entity.Activity
import com.artemkaxboy.redmineexporter.entity.Priority
import com.artemkaxboy.redmineexporter.repository.ActivityRepository
import com.artemkaxboy.redmineexporter.repository.PriorityRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class PriorityServiceTest {

    @Autowired
    lateinit var priorityRepository: PriorityRepository

    @Autowired
    lateinit var priorityService: PriorityService

    val testPriorities = listOf(
        Priority.make(name = "Super High"),
        Priority.make(name = "High"),
        Priority.make(name = "Normal"),
        Priority.make(name = "Low")
    )

    @AfterEach
    fun clearRepository() {
        priorityRepository.deleteAllInBatch()
        priorityService.reset()
    }

    @Test
    fun `returns no priorities before fetch`() {

        priorityRepository.saveAll(testPriorities)

        val got = priorityService.getAllPriorities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all priorities after fetch`() {

        val expected = priorityRepository.saveAll(testPriorities)
        priorityService.fetchPriorities()

        val got = priorityService.getAllPriorities()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `does not return inactive priority`() {

        priorityRepository.save(Priority.make(active = ACTIVE_IS_INACTIVE))
        priorityService.fetchPriorities()

        val got = priorityService.getAllPriorities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `does not return project special priority`() {

        priorityRepository.save(Priority.make(projectId = 1))
        priorityService.fetchPriorities()

        val got = priorityService.getAllPriorities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `clears all priorities by reset`() {

        priorityRepository.saveAll(testPriorities)
        priorityService.fetchPriorities()
        priorityService.reset()

        val got = priorityService.getAllPriorities()
        Assertions.assertThat(got).isEmpty()
    }
}
