package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.Version
import mu.KotlinLogging
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class ClosedVersionDetectedListener(private val metricsRegistry: MetricsRegistry) :
    ApplicationListener<ClosedVersionDetectedEvent> {

    override fun onApplicationEvent(event: ClosedVersionDetectedEvent) {
        metricsRegistry.unregisterMetersForVersion(event.version)
        logger.info { "Done" }
    }
}

class ClosedVersionDetectedEvent(val version: Version) : ApplicationEvent(Unit)

@Component
class AddedMetricDetectedListener(private val metricsRegistry: MetricsRegistry) :
    ApplicationListener<AddedMetricDetectedEvent> {

    override fun onApplicationEvent(event: AddedMetricDetectedEvent) {
        metricsRegistry.registerMeters(event.version, event.counters)
    }
}

class AddedMetricDetectedEvent(val version: Version, val counters: Map<IssueStatus, Long>) : ApplicationEvent(Unit)

