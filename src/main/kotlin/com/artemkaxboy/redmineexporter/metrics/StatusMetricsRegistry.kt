package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.IssueService
import com.artemkaxboy.redmineexporter.service.IssueStatusService
import com.artemkaxboy.redmineexporter.service.VersionService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
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
) {

    private val meters = mutableMapOf<Version, List<Meter.Id>>()

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

        versionService.getVersionList().forEach { version ->

            registerMetersForVersion(version)
        }
    }

    fun registerMetersForVersion(version: Version) {

        meters[version] = issueStatusService.getAllStatuses().map { issueStatus ->
            Gauge
                .builder(REDMINE_PROJECT_ISSUES) {
                    issueService.getMetricByVersionIdAndStatusId(version.id, issueStatus.id)
                }
                .tags(
                    PROJECT_TAG, "${version.project?.name}",
                    VERSION_TAG, version.name,
                    STATUS_TAG, issueStatus.name,
                    CLOSED_TAG, "${issueStatus.isClosed}",
                )
                .register(meterRegistry)
                .id
        }
    }

    fun unregisterMetersForVersion(closedVersion: Version) {
        logger.debug {
            "Remove all meters for version: " +
                    "project (#${closedVersion.projectId} ${closedVersion.project?.name}) " +
                    "version (#${closedVersion.id} ${closedVersion.name})"
        }

        meters[closedVersion]?.forEach { meterId ->
            meterRegistry.remove(meterId)
        }
    }
}
