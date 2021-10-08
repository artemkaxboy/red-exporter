package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Activity
import com.artemkaxboy.redmineexporter.repository.ActivityRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class ActivityService(

    private val activityRepository: ActivityRepository,
) {

    private var activities = emptyList<Activity>()

    fun fetchActivities() {
        activities = activityRepository.findAllShared()
    }

    fun getAllActivities(): List<Activity> = activities

    /**
     * Resets all fetched metrics.
     */
    @TestOnly
    fun reset() {
        activities = emptyList()
    }
}
