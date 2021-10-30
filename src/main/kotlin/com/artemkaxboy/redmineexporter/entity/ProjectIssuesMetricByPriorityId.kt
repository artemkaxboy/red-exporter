package com.artemkaxboy.redmineexporter.entity

class ProjectIssuesMetricByPriorityId(
    val priorityId: Long,
    val metric: Long,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(priorityId = $priorityId , metric = $metric )"
    }
}
