package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.metrics.MeterData
import com.artemkaxboy.redmineexporter.metrics.MetricRegistrationEvent
import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository,
    private val issueStatusService: IssueStatusService,
    private val versionService: VersionService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private val metrics = mutableMapOf<Long, Map<Long, Long>>()

    fun resetMetrics() {
        metrics.clear()
    }

    fun loadProjectCounters(projectId: Long) {
        versionService.getVersion()
    }

    fun loadVersionCounters(versionId: Long) {
        val currentCounters = issueRepository.countByFixedVersionIdGroupedByStatus(versionId)
        val version = versionService.getVersion(versionId)

        if (version != null) {

            currentCounters
                .map {
                    val status = issueStatusService.getStatus(it.key)

                    MeterData(
                        projectName = version.project!!.name,
                        versionId = versionId,
                        versionName = version.name,
                        statusId = it.key,
                        statusName = status.name,
                        statusIsClosed = status.isClosed,
                    )
                }
                .also { applicationEventPublisher.publishEvent(MetricRegistrationEvent(true, it)) }

            metrics[versionId] = currentCounters
        }
    }

    fun isVersionCountersLoaded(versionId: Long): Boolean = metrics.containsKey(versionId)

    fun getCountByVersionIdAndStatusId(versionId: Long, statusId: Long): Long {
        if (!isVersionCountersLoaded(versionId)) {
            loadVersionCounters(versionId)
        }

        return metrics[versionId]?.get(statusId) ?: 0
    }
}
