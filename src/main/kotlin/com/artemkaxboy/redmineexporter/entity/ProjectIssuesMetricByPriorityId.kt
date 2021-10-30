package com.artemkaxboy.redmineexporter.entity

class ProjectIssuesMetricByPriorityId(
    val priorityId: Long,
    val isClosed: Int,
    val metric: Long,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(priorityId = $priorityId , isClosed = $isClosed , metric = $metric )"
    }
}
