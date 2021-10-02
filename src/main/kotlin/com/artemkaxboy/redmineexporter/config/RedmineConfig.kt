package com.artemkaxboy.redmineexporter.config

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import org.springframework.context.annotation.Configuration

@Configuration
class RedmineConfig(val properties: RedmineProperties)
