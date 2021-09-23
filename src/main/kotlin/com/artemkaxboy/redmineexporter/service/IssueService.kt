package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository
) {

    private val metrics = mutableMapOf<Long, Map<Long, Long>>()

    fun resetMetrics() {
        metrics.clear()
    }

    fun loadVersionCounters(versionId: Long) {
        metrics[versionId] = issueRepository.countByFixedVersionIdGroupedByStatus(versionId)
    }

    fun isVersionCountersLoaded(versionId: Long): Boolean = metrics.containsKey(versionId)

    fun getCountByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        if (!isVersionCountersLoaded(versionId)) {
            loadVersionCounters(versionId)
        }

        return metrics[versionId]?.get(statusId) ?: 0
    }
}
