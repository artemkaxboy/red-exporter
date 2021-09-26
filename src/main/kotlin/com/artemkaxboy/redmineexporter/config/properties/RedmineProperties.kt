package com.artemkaxboy.redmineexporter.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "redmine")
class RedmineProperties(

    /**
     * Project ids to meter.
     */
    val projects: List<Long> = emptyList(),
)
