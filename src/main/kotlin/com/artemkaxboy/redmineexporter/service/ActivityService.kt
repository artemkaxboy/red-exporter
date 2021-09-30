package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Activity
import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.ActivityRepository
import org.springframework.stereotype.Service

@Service
class ActivityService(

    private val activityRepository: ActivityRepository,
) {

    private var activities = emptyList<Activity>()

    fun fetchActivities() {
        activities = activityRepository.findAllShared()
        println(activities)
    }

    fun getAllActivities(): List<Activity> = activities
}
