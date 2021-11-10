package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.IssueCategory
import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.*
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
    private val issueCategoryService: IssueCategoryService,

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
        issueCategoryService.fetchCategories()
    }

    private fun registerMetersForVersion(openedVersion: Version) {

        metersByVersion[openedVersion] = registerIssuesByIssueStatusMeters(openedVersion) +
                registerIssuesByPriorityMeters(openedVersion) +
                registerIssuesByTrackerMeters(openedVersion) +
                registerIssuesByCategoriesMeters(openedVersion)
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
                    VERSION_DATE_TAG, version.effectiveDateString,
                    STATUS_TAG, issueStatus.name,
                    CLOSED_TAG, "${issueStatus.isClosed}",
                )
                .register(meterRegistry)
                .id
        }
    }

    private fun registerIssuesByTrackerMeters(version: Version): List<Meter.Id> {

        return issueTrackerService.getAllTrackers().flatMap { tracker ->
            IssueStatus.isClosedVariants.map { isClosed ->
                Gauge
                    .builder(REDMINE_PROJECT_ISSUES_TRACKER) {
                        issueService.getMetricByVersionIdAndIssueTrackerId(version.id, tracker.id, isClosed)
                    }
                    .tags(
                        PROJECT_TAG, "${version.project?.name}",
                        VERSION_TAG, version.name,
                        VERSION_DATE_TAG, version.effectiveDateString,
                        TRACKER_TAG, tracker.name,
                        CLOSED_TAG, "$isClosed",
                    )
                    .register(meterRegistry)
                    .id
            }
        }
    }

    private fun registerIssuesByPriorityMeters(version: Version): List<Meter.Id> {
        return issuePriorityService.getAllPriorities().flatMap { priority ->
            IssueStatus.isClosedVariants.map { isClosed ->
                Gauge
                    .builder(REDMINE_PROJECT_ISSUES_PRIORITY) {
                        issueService.getMetricByVersionIdAndPriorityIdAndIsClosed(version.id, priority.id, isClosed)
                    }
                    .tags(
                        PROJECT_TAG, "${version.project?.name}",
                        VERSION_TAG, version.name,
                        VERSION_DATE_TAG, version.effectiveDateString,
                        PRIORITY_TAG, priority.name,
                        CLOSED_TAG, "$isClosed",
                    )
                    .register(meterRegistry)
                    .id
            }
        }
    }

    private fun registerIssuesByCategoriesMeters(version: Version): List<Meter.Id> {
        return (issueCategoryService.getAllCategories(version.projectId) + IssueCategory.emptyCategory).flatMap { category ->
            IssueStatus.isClosedVariants.map { isClosed ->
                Gauge
                    .builder(REDMINE_PROJECT_ISSUES_CATEGORY) {
                        issueService.getMetricByVersionIdAndCategoryId(version.id, category.id, isClosed)
                    }
                    .tags(
                        PROJECT_TAG, "${version.project?.name}",
                        VERSION_TAG, version.name,
                        VERSION_DATE_TAG, version.effectiveDateString,
                        CATEGORY_TAG, category.name,
                        CLOSED_TAG, "$isClosed",
                    )
                    .register(meterRegistry)
                    .id
            }
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
