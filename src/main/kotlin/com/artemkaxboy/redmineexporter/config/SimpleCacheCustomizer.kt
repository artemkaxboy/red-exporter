package com.artemkaxboy.redmineexporter.config

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.stereotype.Component

@Component
@EnableCaching
class SimpleCacheCustomizer : CacheManagerCustomizer<ConcurrentMapCacheManager> {

    override fun customize(cacheManager: ConcurrentMapCacheManager) {
        cacheManager.setCacheNames(listOf("catalog"))
    }
}
