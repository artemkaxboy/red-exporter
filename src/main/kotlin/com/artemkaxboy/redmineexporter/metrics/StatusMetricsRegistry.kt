package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.User
import com.artemkaxboy.redmineexporter.service.*
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.stereotype.Component

const val PROJECT_TAG = "project"
const val VERSION_TAG = "version"
const val VERSION_DATE_TAG = "version_date"
const val CLOSED_TAG = "closed"

const val REDMINE_PROJECT_ISSUES = "redmine_project_issues"
const val STATUS_TAG = "status"

const val REDMINE_PROJECT_ISSUES_PRIORITY = "redmine_project_issues_priority"
const val PRIORITY_TAG = "priority"

const val REDMINE_PROJECT_ISSUES_TRACKER = "redmine_project_issues_tracker"
const val TRACKER_TAG = "tracker"

const val REDMINE_PROJECT_ISSUES_CATEGORY = "redmine_project_issues_category"
const val CATEGORY_TAG = "category"

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

    private val activityService: ActivityService,

    private val versionMeters: VersionMeters,
) {

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
        activityService.fetchActivities()
        versionMeters.fetchCatalogs()
    }

    private fun fetchDynamicData() {
        versionService.fetchVersionsForPreconfiguredProjects()
        userService.fetchAllUsers()
        issueService.fetchMetrics(versionService.getAllVersions())
        timeEntryService.fetchMetrics(userService.getAllUsers())
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
