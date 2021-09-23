package com.artemkaxboy.redmineexporter.metrics

data class MeterData(
    val projectName: String,
    val versionId: Long,
    val versionName: String,
    val statusId: Long,
    val statusName: String,
    val statusIsClosed: Int,
)
