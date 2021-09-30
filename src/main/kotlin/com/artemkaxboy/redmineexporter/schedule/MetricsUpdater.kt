package com.artemkaxboy.redmineexporter.schedule

import com.artemkaxboy.redmineexporter.metrics.StatusMetricsRegistry
import mu.KotlinLogging
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

const val SECOND = 1_000L
const val MINUTE = 60 * SECOND
//const val CACHE_TTL = 10 * SECOND
const val CACHE_TTL = 10 * MINUTE

private val logger = KotlinLogging.logger {}

@Component
@EnableScheduling
class MetricsUpdater(
    private val statusMetricsRegistry: StatusMetricsRegistry,
) {

    @Scheduled(fixedDelay = CACHE_TTL, initialDelay = 0)
    fun updateMetrics() {
        logger.debug { "Scheduler run: `updateMetrics`" }
        statusMetricsRegistry.fetchAllMetrics()
        logger.debug { "Scheduler finished: `updateMetrics`" }
    }
}
