package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.ActivityService
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
//    private val

    private val issueStatusService: IssueStatusService,
    private val activityService: ActivityService,
) {

    private val meters = mutableMapOf<Version, List<Meter.Id>>()

    /**
     * Loads all metrics. Involves:
     * * updating static catalogs
     * * fetching opened version for all projects
     * * fetching metrics data
     */
    fun fetchAllMetrics() {

        fetchStaticCatalogs()
        fetchDynamicData()
    }

    private fun fetchStaticCatalogs() {
        issueStatusService.fetchStatuses()
        activityService.fetchActivities()
    }

    private fun fetchDynamicData() {
        versionService.fetchVersions()
        issueService.fetchMetrics(versionService.getAllVersions())


    }

    fun registerMetersForVersion(openedVersion: Version) {

        meters[openedVersion] = issueStatusService.getAllStatuses().map { issueStatus ->
            Gauge
                .builder(REDMINE_PROJECT_ISSUES) {
                    issueService.getMetricByVersionIdAndStatusId(openedVersion.id, issueStatus.id)
                }
                .tags(
                    PROJECT_TAG, "${openedVersion.project?.name}",
                    VERSION_TAG, openedVersion.name,
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
