package com.artemkaxboy.redmineexporter.entity

class UserHoursMetricByActivity(
    val activityId: Long,
    val hours: Double,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(activityId = $activityId , hours = $hours )"
    }
}
