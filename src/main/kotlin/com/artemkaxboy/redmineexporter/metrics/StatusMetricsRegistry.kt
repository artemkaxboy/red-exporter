package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.User
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.service.*
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.stereotype.Component

const val REDMINE_PROJECT_ISSUES = "redmine_project_issues"
const val STATUS_TAG = "status"
const val PROJECT_TAG = "project"
const val VERSION_TAG = "version"
const val CLOSED_TAG = "closed"

const val REDMINE_USER_ACTIVITIES = "redmine_user_activities"
const val LOGIN_TAG = "login"
const val NAME_TAG = "name"
const val ACTIVITY_TAG = "activity"

private val logger = KotlinLogging.logger {}

@Component
class StatusMetricsRegistry(

    private val meterRegistry: MeterRegistry,
    private val versionService: VersionService,
    private val userService: UserService,
    private val issueService: IssueService,
    private val timeEntryService: TimeEntryService,

    private val issueStatusService: IssueStatusService,
    private val activityService: ActivityService,
) {

    private val meters = mutableMapOf<Version, List<Meter.Id>>()
    private val userMeters = mutableMapOf<User, List<Meter.Id>>()

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
        versionService.fetchVersionsForPreconfiguredProjects()
        userService.fetchAllUsers()
        issueService.fetchMetrics(versionService.getAllVersions())
        timeEntryService.fetchMetrics(userService.getAllUsers())
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

    fun registerMetersForUser(addedUser: User) {

        userMeters[addedUser] = activityService.getAllActivities().map { activity ->
            Gauge
                .builder(REDMINE_USER_ACTIVITIES) {
                    timeEntryService.getMetricByUserIdAndActivityId(addedUser.id, activity.id)
                }
                .tags(
                    LOGIN_TAG, addedUser.login,
                    NAME_TAG, addedUser.name,
                    ACTIVITY_TAG, activity.name,
                )
                .register(meterRegistry)
                .id
        }
    }

    fun unregisterMetersForUser(deletedUser: User) {
        logger.debug { "Remove all meters for user (#${deletedUser.id} ${deletedUser.login})" }

        userMeters[deletedUser]?.forEach { meterId ->
            meterRegistry.remove(meterId)
        }
    }

}
