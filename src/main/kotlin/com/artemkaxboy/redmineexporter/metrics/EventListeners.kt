package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * Listener should be notified each time when any project version has been closed.
 * All meters for version should be removed.
 */
@Component
class VersionClosedEventListener(private val statusMetricsRegistry: StatusMetricsRegistry) :
    ApplicationListener<VersionClosedEventListener.Event> {

    override fun onApplicationEvent(event: Event) {
        statusMetricsRegistry.unregisterMetersForVersion(event.version)
    }

    class Event(val version: Version) : ApplicationEvent(Unit)
}

/**
 * Listener should be notified each time when metrics read from DB.
 * 0 meters should be deleted, new non-zero meters should be created.
 */
@Component
class MetricsUpdatedEventListener(private val statusMetricsRegistry: StatusMetricsRegistry) :
    ApplicationListener<MetricsUpdatedEventListener.Event> {

    override fun onApplicationEvent(event: Event) {
        statusMetricsRegistry.registerMeters(event.version, event.metrics)
    }

    class Event(val version: Version, val metrics: Map<IssueStatus, Long>) : ApplicationEvent(Unit)
}


