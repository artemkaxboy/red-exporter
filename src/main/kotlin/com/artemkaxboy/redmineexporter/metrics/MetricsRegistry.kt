package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.service.IssueService
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

const val STATUS_TAG = "status"
const val PROJECT_TAG = "project"
const val VERSION_TAG = "version"
const val CLOSED_TAG = "closed"

const val REDMINE_PROJECT_ISSUES = "redmine_project_issues"

private val logger = KotlinLogging.logger {}

@Component
class MetricsRegistry(

    private val issueService: IssueService,
    private val redmineProperties: RedmineProperties,
    private val meterRegistry: MeterRegistry,
) {

    val meters = mutableMapOf<Pair<Long, Long>, Meter.Id>()

    @EventListener(ApplicationReadyEvent::class)
    fun initMeters() {

        redmineProperties.projects.forEach {
            issueService.loadVersionCounters(it)
        }
    }

    fun updateMeterRegistration(exists: Boolean, metersData: List<MeterData>) {
        if (exists) {
            registerMeter(metersData)
        } else {
            unregisterMeter(metersData)
        }
    }

    private fun unregisterMeter(metersData: List<MeterData>) {
        metersData.forEach { meterData ->
            val meterKey = meterData.versionId to meterData.statusId
            meters[meterKey]
                ?.also { meterRegistry.remove(it) }
                ?.also { logger.debug { "Remove meter for ${meterData.projectName} ${meterData.statusName}" } }
        }
    }

    private fun registerMeter(metersData: List<MeterData>) {
        metersData.forEach { meterData ->
            val meterKey = meterData.versionId to meterData.statusId

            if (!meters.containsKey(meterKey)) {
                Gauge
                    .builder(REDMINE_PROJECT_ISSUES) {
                        issueService.getCountByVersionIdAndStatusId(meterData.versionId, meterData.statusId)
                    }
                    .tags(
                        PROJECT_TAG, meterData.projectName,
                        VERSION_TAG, meterData.versionName,
                        STATUS_TAG, meterData.statusName,
                        CLOSED_TAG, "${meterData.statusIsClosed}",
                    )
                    .register(meterRegistry)
                    .also { meters[meterKey] = it.id }
                    .also { logger.debug { "Add meter for ${meterData.projectName} ${meterData.statusName}" } }
            }
        }
    }
}
