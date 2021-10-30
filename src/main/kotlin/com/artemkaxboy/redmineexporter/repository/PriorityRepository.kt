package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface PriorityRepositoryI : JpaRepository<Priority, Long> {

    fun findByTypeIsAndActiveIsAndProjectIdIsNull(type: String, active: Int): List<Priority>
}

@Repository
class PriorityRepository(private val priorityRepositoryI: PriorityRepositoryI) :
    PriorityRepositoryI by priorityRepositoryI {

    fun findAllShared(): List<Priority> {
        return findByTypeIsAndActiveIsAndProjectIdIsNull(ISSUE_PRIORITY, ACTIVE_IS_ACTIVE)
    }
}
