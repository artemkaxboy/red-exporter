package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import com.artemkaxboy.redmineexporter.service.IssueService
import com.artemkaxboy.redmineexporter.service.IssueStatusService
import com.artemkaxboy.redmineexporter.service.VersionService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

const val VERSION_TAG = "version"

@Component
class MetricsRegistry(

    private val redmineProperties: RedmineProperties,

    private val issueService: IssueService,
    private val issueStatusService: IssueStatusService,
    private val versionService: VersionService,

    private val meterRegistry: MeterRegistry,
) {

    @PostConstruct
    private fun initMeters() {

        versionService.getAll().forEach {
            initVersion(it.id, it.name)
        }
    }

    fun initVersion(versionId: Long, versionName: String) {

        issueStatusService.getAll().forEach {
            Gauge
                .builder("issues_by_status") {
                    issueService.countByStatusId(versionId, it.id)
                }
                .tags("status", it.name, VERSION_TAG, versionName)
                .register(meterRegistry)
        }

        listOf(false to "opened", true to "closed").forEach {
            Gauge
                .builder("issues_by_opened") {
                    issueService.countByClosed(versionId, it.first)
                }
                .tags("status", it.second, VERSION_TAG, versionName)
                .register(meterRegistry)
        }
    }
}
