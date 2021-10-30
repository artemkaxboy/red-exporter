package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueCategory
import com.artemkaxboy.redmineexporter.repository.IssueCategoryRepository
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class IssueCategoryService(

    private val issueCategoryRepository: IssueCategoryRepository,
) {

    private var categories = emptyMap<Long, List<IssueCategory>>()

    fun fetchCategories() {

        categories = issueCategoryRepository.findAll().groupBy { it.projectId }
    }

    fun getAllCategories(projectId: Long): List<IssueCategory> = categories.getOrDefault(projectId, emptyList())

    /**
     * Resets all fetched metrics.
     */
    @TestOnly
    fun reset() {
        categories = emptyMap()
    }
}
