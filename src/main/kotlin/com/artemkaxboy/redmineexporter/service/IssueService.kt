package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByPriorityId
import com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByStatusId
import com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByTrackerId
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

/**
 * Service provides function to get instant issue metrics.
 */
@Service
class IssueService(

    private val issueRepository: IssueRepository,
) {

    /**
     * Map containing live data values (Map: <VersionID <IssueStatusID, LiveDataMetrics>>).
     */
    private val metricsByVersionByIssueStatus = mutableMapOf<Long, List<ProjectIssuesMetricByStatusId>>()

    /**
     * Map containing live data values (Map: <VersionID <IssuePriorityID, LiveDataMetrics>>).
     */
    private val metricsByVersionByIssuePriority = mutableMapOf<Long, List<ProjectIssuesMetricByPriorityId>>()

    /**
     * Map containing live data values (Map: <VersionID <IssueTrackerID, LiveDataMetrics>>).
     */
    private val metricsByVersionByIssueTracker = mutableMapOf<Long, List<ProjectIssuesMetricByTrackerId>>()

    /**
     * Returns last fetched metrics by given params. All metrics are empty before calling [fetchMetrics].
     *
     * @param versionId project version id to get metric for
     * @param statusId issue status (New, InProgress, Frozen, etc.) id to get metric for
     */
    fun getMetricByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        return metricsByVersionByIssueStatus[versionId]?.find { it.statusId == statusId }?.metric ?: 0
    }

    /**
     * Returns last fetched metrics by given params. All metrics are empty before calling [fetchMetrics].
     *
     * @param versionId project version id to get metric for
     * @param priorityId priority (High, Normal, Low, etc.) id to get metric for
     */
    fun getMetricByVersionIdAndPriorityId(versionId: Long, priorityId: Long): Long {
        return metricsByVersionByIssuePriority[versionId]?.find { it.priorityId == priorityId }?.metric ?: 0
    }

    /**
     * Returns last fetched metrics by given params. All metrics are empty before calling [fetchMetrics].
     *
     * @param versionId project version id to get metric for
     * @param priorityId priority (High, Normal, Low, etc.) id to get metric for
     */
    fun getMetricByVersionIdAndIssueTrackerId(versionId: Long, trackerId: Long): Long {
        return metricsByVersionByIssueTracker[versionId]?.find { it.trackerId == trackerId }?.metric ?: 0
    }

    /**
     * Fetches all metrics from DB for all given versions.
     * @param versions list of project versions to fetch metrics for
     */
    fun fetchMetrics(versions: List<Version>) {
        versions
            .sortedBy { it.id } // for better logs reading
            .forEach {
                fetchVersionMetrics(it)
            }
    }

    private fun fetchVersionMetrics(version: Version) {

        fetchVersionMetricsGroupedByIssueStatus(version.id)
        fetchVersionMetricsGroupedByIssuePriority(version.id)
    }

    private fun fetchVersionMetricsGroupedByIssueStatus(versionId: Long) {
        metricsByVersionByIssueStatus[versionId] = issueRepository.sumByFixedVersionIdGroupedByIssueStatus(versionId)
    }

    private fun fetchVersionMetricsGroupedByIssuePriority(versionId: Long) {
        metricsByVersionByIssuePriority[versionId] = issueRepository.sumByFixedVersionIdGroupedByPriority(versionId)
    }

    /**
     * Resets all fetched metrics.
     */
    @TestOnly
    fun reset() {
        metricsByVersionByIssueStatus.clear()
    }
}
