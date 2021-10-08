package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.ACTIVE_IS_INACTIVE
import com.artemkaxboy.redmineexporter.entity.Activity
import com.artemkaxboy.redmineexporter.repository.ActivityRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class ActivityServiceTest {

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityService: ActivityService

    val testActivities = listOf(
        Activity.make(id = 1, name = "Analysis"),
        Activity.make(id = 2, name = "Code"),
        Activity.make(id = 3, name = "Code Review"),
        Activity.make(id = 4, name = "Test")
    )

    @AfterEach
    fun clearRepository() {
        activityRepository.deleteAllInBatch()
        activityService.reset()
    }

    @Test
    fun `returns no activities before fetch`() {

        activityRepository.saveAll(testActivities)

        val got = activityService.getAllActivities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all activities after fetch`() {

        val expected = activityRepository.saveAll(testActivities)
        activityService.fetchActivities()

        val got = activityService.getAllActivities()
        Assertions.assertThat(got).hasSameElementsAs(expected)
    }

    @Test
    fun `does not return inactive activity`() {

        activityRepository.save(Activity.make(active = ACTIVE_IS_INACTIVE))
        activityService.fetchActivities()

        val got = activityService.getAllActivities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `does not return project special activity`() {

        activityRepository.save(Activity.make(projectId = 1))
        activityService.fetchActivities()

        val got = activityService.getAllActivities()
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `clears all activities by reset`() {

        activityRepository.saveAll(testActivities)
        activityService.fetchActivities()
        activityService.reset()

        val got = activityService.getAllActivities()
        Assertions.assertThat(got).isEmpty()
    }
}
