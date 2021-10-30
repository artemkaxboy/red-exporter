package com.artemkaxboy.redmineexporter.entity

class ProjectIssuesMetricByCategoryId(
    val categoryId: Long?,
    val isClosed: Int,
    val metric: Long,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(categoryId = $categoryId , isClosed = $isClosed , metric = $metric )"
    }
}
