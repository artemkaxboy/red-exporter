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
import javax.annotation.PostConstruct

const val STATUS_TAG = "status"
const val PROJECT_TAG = "project"
const val VERSION_TAG = "version"
const val CLOSED_TAG = "closed"

private val logger = KotlinLogging.logger {}

@Component
class MetricsRegistry(

    private val issueService: IssueService,
    private val issueStatusService: IssueStatusService,
    private val versionService: VersionService,

    private val meterRegistry: MeterRegistry,
) {

    val meters = mutableMapOf<Long, Meter.Id>()

    @PostConstruct
    private fun initMeters() {

        versionService.getAll().forEach {
            logger.info { "Initialize metrics for project `${it.project?.name}` version `${it.name}`" }
            initVersion(it)
        }
    }

    fun initVersion(version: Version) {

        issueStatusService.getAll().forEach { issueStatus ->

            Gauge
                .builder("redmine_project_issues") {
                    issueService.countByStatusId(version.id, issueStatus.id)
                }
                .tags(
                    STATUS_TAG, issueStatus.name,
                    VERSION_TAG, version.name,
                    CLOSED_TAG, "${issueStatus.isClosed}",
                    PROJECT_TAG, "${version.project?.name}"
                )
                .register(meterRegistry)
                .also { meter -> meters[version.id] = meter.id }
        }
    }
}
