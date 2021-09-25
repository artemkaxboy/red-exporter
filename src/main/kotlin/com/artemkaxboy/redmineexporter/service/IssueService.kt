package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.metrics.AddedMetricDetectedEvent
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository,
    private val issueStatusService: IssueStatusService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private val metrics = mutableMapOf<Long, Map<Long, Long>>()

    /**
     * Loads counters from DB for given version (todo don't do it here `and publish update event`)
     * @param version version to load counters
     */
    private fun loadVersionCounters(version: Version) {
        val currentCounters = issueRepository.countByFixedVersionIdGroupedByStatus(version.id)

        applicationEventPublisher.publishEvent(
            AddedMetricDetectedEvent(
                version,
                currentCounters.mapKeys { issueStatusService.getStatus(it.key) })
        )

        metrics[version.id] = currentCounters
    }

    fun getCountByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        return metrics[versionId]?.get(statusId) ?: 0
    }

    fun updateCounters(versionList: List<Version>) {
        versionList
            .sortedBy { it.id } // for better logs reading
            .forEach {
                loadVersionCounters(it)
            }
    }
}
