package com.artemkaxboy.redmineexporter.entity

class ProjectIssuesMetricByStatusId(
    val statusId: Long,
    val metric: Long,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(statusId = $statusId , metric = $metric )"
    }
}
