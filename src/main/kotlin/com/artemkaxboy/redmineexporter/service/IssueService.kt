package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.entity.VersionWithMetrics
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository,
) {

    /**
     * Map containing live data values (Map: <VersionID <IssueStatusID, LiveDataMetrics>>).
     */
    private val metricsByVersionByStatus = mutableMapOf<Long, Map<Long, Long>>()

    private fun loadVersionMetrics(version: Version) {
        val currentMetrics = issueRepository.countByFixedVersionIdGroupedByStatus(version.id)

        metricsByVersionByStatus[version.id] = currentMetrics.associate { it.statusId to it.metric }
    }

    fun getMetricByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        return metricsByVersionByStatus[versionId]?.get(statusId) ?: 0
    }

    /**
     * Loads metrics from DB for all given versions.
     * @param versions list of versions to load metrics for
     */
    fun loadMetrics(versions: List<Version>) {
        versions
            .sortedBy { it.id } // for better logs reading
            .forEach {
                loadVersionMetrics(it)
            }
    }

    /**
     * Returns map of loaded metrics (Map <VersionID, Set<StatusID>>).
     */
    fun getAvailableMetrics(): Map<Long, Set<Long>> {

        return metricsByVersionByStatus.mapValues { it.value.keys }
    }
}
