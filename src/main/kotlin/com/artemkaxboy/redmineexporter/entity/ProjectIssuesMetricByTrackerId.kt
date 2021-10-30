package com.artemkaxboy.redmineexporter.entity

class ProjectIssuesMetricByTrackerId(
    val trackerId: Long,
    val isClosed: Int,
    val metric: Long,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(trackerId = $trackerId , isClosed = $isClosed , metric = $metric )"
    }
}
