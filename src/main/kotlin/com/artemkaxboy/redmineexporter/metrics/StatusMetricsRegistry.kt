package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.IssueService
import com.artemkaxboy.redmineexporter.service.IssueStatusService
import com.artemkaxboy.redmineexporter.service.VersionService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

const val STATUS_TAG = "status"
const val PROJECT_TAG = "project"
const val VERSION_TAG = "version"
const val CLOSED_TAG = "closed"

const val REDMINE_PROJECT_ISSUES = "redmine_project_issues"

private val logger = KotlinLogging.logger {}

@Component
class StatusMetricsRegistry(

    private val issueService: IssueService,
    private val meterRegistry: MeterRegistry,
    private val versionService: VersionService,
    private val issueStatusService: IssueStatusService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private val meters = mutableMapOf<Version, MutableMap<IssueStatus, Meter.Id>>()

    /**
     * Loads all metrics. Involves:
     * * getting all active versions for each following project
     * * loading all available metrics for each found version
     * * generate per version `metrics updated` event containing metrics list
     */
    fun loadAllMetrics() {

        versionService.updateVersions()
        issueStatusService.updateStatuses()
        issueService.loadMetrics(versionService.getVersionList())

        issueService.getAvailableMetrics().forEach { (versionId, metricsByStatus) ->

            val version = versionService.getVersion(versionId) ?: return@forEach
            val metrics = metricsByStatus.associate { statusId ->
                issueStatusService.getStatus(statusId) to
                        issueService.getMetricByVersionIdAndStatusId(versionId, statusId)
            }

            applicationEventPublisher.publishEvent(MetricsUpdatedEventListener.Event(version, metrics))
        }
    }

    /**
     * Updates meter list for version. Deletes 0 meters, create new non-zero meters.
     *
     * @param version version to create metrics for
     * @param metrics non-0 metrics for version
     */
    fun registerMeters(version: Version, metrics: Map<IssueStatus, Long>) {

        val existingMeters = meters.getOrPut(version) { mutableMapOf() }
        val existingMetersStatuses = existingMeters.keys
        val foundStatuses = metrics.keys

        (existingMetersStatuses - foundStatuses)
            .sortedBy { it.id } // for better logs reading
            .forEach { deletedStatus ->

                existingMeters[deletedStatus]
                    ?.also { meterRegistry.remove(it) }
                    ?.also { existingMeters.remove(deletedStatus) }
                    ?.also {
                        logger.debug {
                            "Meter removed: " +
                                    "project (#${version.projectId} ${version.project?.name}) " +
                                    "version (#${version.id} ${version.name}) " +
                                    "status (#${deletedStatus.id} ${deletedStatus.name})"
                        }
                    }
            }

        (foundStatuses - existingMetersStatuses)
            .sortedBy { it.id } // for better logs reading
            .forEach { newStatus ->

                Gauge
                    .builder(REDMINE_PROJECT_ISSUES) {
                        issueService.getMetricByVersionIdAndStatusId(version.id, newStatus.id)
                    }
                    .tags(
                        PROJECT_TAG, "${version.project?.name}",
                        VERSION_TAG, version.name,
                        STATUS_TAG, newStatus.name,
                        CLOSED_TAG, "${newStatus.isClosed}",
                    )
                    .register(meterRegistry)
                    .also { existingMeters[newStatus] = it.id }
                    .also {
                        logger.debug {
                            "Meter added: " +
                                    "project (#${version.projectId} ${version.project?.name}) " +
                                    "version (#${version.id} ${version.name}) " +
                                    "status (#${newStatus.id} ${newStatus.name})"
                        }
                    }

            }
    }

    fun unregisterMetersForVersion(closedVersion: Version) {
        logger.debug {
            "Remove all meters for version: " +
                    "project (#${closedVersion.projectId} ${closedVersion.project?.name}) " +
                    "version (#${closedVersion.id} ${closedVersion.name})"
        }

        meters[closedVersion]?.forEach { (issueStatus, meterId) ->
            meterRegistry.remove(meterId)
            logger.debug {
                "Meter removed: " +
                        "project (#${closedVersion.projectId} ${closedVersion.project?.name}) " +
                        "version (#${closedVersion.id} ${closedVersion.name}) " +
                        "status (#${issueStatus.id} ${issueStatus.name})"
            }
        }
    }
}
