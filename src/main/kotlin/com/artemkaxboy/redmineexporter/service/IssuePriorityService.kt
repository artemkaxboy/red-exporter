package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Priority
import com.artemkaxboy.redmineexporter.repository.PriorityRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class IssuePriorityService(

    private val priorityRepository: PriorityRepository,
) {

    private var priorities = emptyList<Priority>()

    fun fetchPriorities() {
        priorities = priorityRepository.findAllShared()
    }

    fun getAllPriorities(): List<Priority> = priorities

    /**
     * Resets all fetched metrics.
     */
    @TestOnly
    fun reset() {
        priorities = emptyList()
    }
}
