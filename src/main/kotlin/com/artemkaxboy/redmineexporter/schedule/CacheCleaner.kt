package com.artemkaxboy.redmineexporter.schedule

import mu.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

const val SECOND = 1_000L
const val MINUTE = 60 * SECOND
const val CACHE_TTL = 10 * MINUTE

private val logger = KotlinLogging.logger {}

@Component
@EnableScheduling
class CacheCleaner {

    @Scheduled(fixedDelay = CACHE_TTL, initialDelay = 0)
    @CacheEvict("metrics", allEntries = true)
    fun evictAll() {
        logger.debug { "Cache cleaned" }
    }
}
