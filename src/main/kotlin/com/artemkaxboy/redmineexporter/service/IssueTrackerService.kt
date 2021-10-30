package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueTracker
import com.artemkaxboy.redmineexporter.repository.IssueTrackerRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class IssueTrackerService(

    private val issueTrackerRepository: IssueTrackerRepository,
) {

    private var trackers = emptyList<IssueTracker>()

    fun fetchTrackers() {
        trackers = issueTrackerRepository.findAll()
    }

    fun getAllTrackers(): List<IssueTracker> = trackers

    /**
     * Resets all fetched metrics.
     */
    @TestOnly
    fun reset() {
        trackers = emptyList()
    }
}
