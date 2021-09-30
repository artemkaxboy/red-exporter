package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.StatusWithMetric
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository,
) {

    /**
     * Map containing live data values (Map: <VersionID <IssueStatusID, LiveDataMetrics>>).
     */
    private val metricsByVersionByStatus = mutableMapOf<Long, List<StatusWithMetric>>()

    fun getMetricByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        return metricsByVersionByStatus[versionId]?.find { it.statusId == statusId }?.metric ?: 0
    }

    /**
     * Loads metrics from DB for all given versions.
     * @param versions list of versions to load metrics for
     */
    fun fetchMetrics(versions: List<Version>) {
        versions
            .sortedBy { it.id } // for better logs reading
            .forEach {
                fetchVersionMetrics(it)
            }
    }

    private fun fetchVersionMetrics(version: Version) {

        val currentMetrics = issueRepository.countByFixedVersionIdGroupedByStatus(version.id)
        metricsByVersionByStatus[version.id] = currentMetrics
    }
}
