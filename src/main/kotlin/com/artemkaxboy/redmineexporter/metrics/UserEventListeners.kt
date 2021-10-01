package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.User
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * Listener should be notified each time when any project version has been closed.
 * All meters for version should be removed.
 */
@Component
class UserDeletedEventListener(private val statusMetricsRegistry: StatusMetricsRegistry) :
    ApplicationListener<UserDeletedEventListener.Event> {

    override fun onApplicationEvent(event: Event) {
        statusMetricsRegistry.unregisterMetersForUser(event.user)
    }

    class Event(val user: User) : ApplicationEvent(Unit)
}

/**
 * Listener should be notified each time when any project version has been opened.
 * Meters for each possible issue status should be created.
 */
@Component
class UserAddedEventListener(private val statusMetricsRegistry: StatusMetricsRegistry) :
    ApplicationListener<UserAddedEventListener.Event> {

    override fun onApplicationEvent(event: Event) {
        statusMetricsRegistry.registerMetersForUser(event.user)
    }

    class Event(val user: User) : ApplicationEvent(Unit)
}

