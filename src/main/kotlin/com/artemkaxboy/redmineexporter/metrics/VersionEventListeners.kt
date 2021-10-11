package com.artemkaxboy.redmineexporter.metrics

import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.context.ApplicationEvent

class VersionClosedEvent(val version: Version): ApplicationEvent(Unit)

class VersionOpenedEvent(val version: Version): ApplicationEvent(Unit)
