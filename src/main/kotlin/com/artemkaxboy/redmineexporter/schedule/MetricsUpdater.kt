package com.artemkaxboy.redmineexporter.schedule

import com.artemkaxboy.redmineexporter.metrics.StatusMetricsRegistry
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
@Profile("!test")
@EnableScheduling
class MetricsUpdater(
    private val statusMetricsRegistry: StatusMetricsRegistry,
) {

    @Scheduled(fixedRateString = "#{@redmineConfig.properties.updateInterval.toMillis()}")
    fun updateMetrics() {
        logger.debug { "Scheduler run: `updateMetrics`" }
        statusMetricsRegistry.fetchAllMetrics()
        logger.debug { "Scheduler finished: `updateMetrics`" }
    }
}
