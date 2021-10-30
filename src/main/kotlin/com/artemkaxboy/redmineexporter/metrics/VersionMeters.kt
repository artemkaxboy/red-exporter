package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.IssueService
import com.artemkaxboy.redmineexporter.service.IssueStatusService
import com.artemkaxboy.redmineexporter.service.IssueTrackerService
import com.artemkaxboy.redmineexporter.service.IssuePriorityService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class VersionMeters(
    // catalogs
    private val issueStatusService: IssueStatusService,
    private val issueTrackerService: IssueTrackerService,
    private val issuePriorityService: IssuePriorityService,

    // data
    private val issueService: IssueService,

    // components
    private val meterRegistry: MeterRegistry,
) {

    private val metersByVersion = mutableMapOf<Version, List<Meter.Id>>()

    @EventListener
    fun versionClosed(event: VersionClosedEvent) {
        unregisterMetersForVersion(event.version)
    }

    @EventListener
    fun versionOpened(event: VersionOpenedEvent) {
        registerMetersForVersion(event.version)
    }

    fun fetchCatalogs() {

        issueStatusService.fetchStatuses()
        issueTrackerService.fetchTrackers()
        issuePriorityService.fetchPriorities()
    }

    private fun registerMetersForVersion(openedVersion: Version) {

        metersByVersion[openedVersion] = registerIssuesByIssueStatusMeters(openedVersion) +
                registerIssuesByPriorityMeters(openedVersion) +
                registerIssuesByTrackerMeters(openedVersion)
    }

    private fun registerIssuesByIssueStatusMeters(version: Version): List<Meter.Id> {
        return issueStatusService.getAllStatuses().map { issueStatus ->
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

    private fun registerIssuesByTrackerMeters(version: Version): List<Meter.Id> {

        return issueTrackerService.getAllTrackers().map { tracker ->
            Gauge
                .builder(REDMINE_PROJECT_ISSUES_TRACKER) {
                    issueService.getMetricByVersionIdAndIssueTrackerId(version.id, tracker.id)
                }
                .tags(
                    PROJECT_TAG, "${version.project?.name}",
                    VERSION_TAG, version.name,
                    TRACKER_TAG, tracker.name,
                )
                .register(meterRegistry)
                .id
        }
    }

    private fun registerIssuesByPriorityMeters(version: Version): List<Meter.Id> {
        return issuePriorityService.getAllPriorities().map { priority ->
            Gauge
                .builder(REDMINE_PROJECT_ISSUES_PRIORITY) {
                    issueService.getMetricByVersionIdAndPriorityId(version.id, priority.id)
                }
                .tags(
                    PROJECT_TAG, "${version.project?.name}",
                    VERSION_TAG, version.name,
                    PRIORITY_TAG, priority.name,
                )
                .register(meterRegistry)
                .id
        }
    }

    private fun unregisterMetersForVersion(closedVersion: Version) {
        logger.debug {
            "Remove all meters for version: " +
                    "project (#${closedVersion.projectId} ${closedVersion.project?.name}) " +
                    "version (#${closedVersion.id} ${closedVersion.name})"
        }

        metersByVersion[closedVersion]?.forEach { meterId ->
            meterRegistry.remove(meterId)
        }
    }

}
