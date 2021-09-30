package com.artemkaxboy.redmineexporter.entity

class VersionWithMetrics(
    val versionId: Long,
    val statusesWithMetrics: List<StatusWithMetric>,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(versionId = $versionId , statusesWithMetrics = $statusesWithMetrics )"
    }
}