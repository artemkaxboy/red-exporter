package com.artemkaxboy.redmineexporter.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

const val DEFAULT_UPDATE_INTERVAL_IN_MINUTES = 10L

@ConstructorBinding
@ConfigurationProperties(prefix = "redmine")
class RedmineProperties(

    /**
     * Metrics update interval.
     */
    val updateInterval: Duration = Duration.ofMinutes(DEFAULT_UPDATE_INTERVAL_IN_MINUTES),

    /**
     * Project ids to meter.
     */
    val projects: List<Long> = emptyList(),

    val users: List<Long> = emptyList(),
)
