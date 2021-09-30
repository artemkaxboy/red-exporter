package com.artemkaxboy.redmineexporter.metrics

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
 * Listener should be notified each time when any project version has been opened.
 * Meters for each possible issue status should be created.
 */
@Component
class VersionOpenedEventListener(private val statusMetricsRegistry: StatusMetricsRegistry) :
    ApplicationListener<VersionOpenedEventListener.Event> {

    override fun onApplicationEvent(event: Event) {
        statusMetricsRegistry.registerMetersForVersion(event.version)
    }

    class Event(val version: Version) : ApplicationEvent(Unit)
}

