package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ActivityRepositoryI : JpaRepository<Activity, Long> {

    fun findByTypeIsAndActiveIsAndProjectIdIsNull(type: String, active: Int): List<Activity>
}

@Repository
class ActivityRepository(private val activityRepositoryI: ActivityRepositoryI) :
    ActivityRepositoryI by activityRepositoryI {

    fun findAllShared(): List<Activity> {
        return findByTypeIsAndActiveIsAndProjectIdIsNull(TIME_ENTRY_ACTIVITY, ACTIVE_IS_ACTIVE)
    }
}
