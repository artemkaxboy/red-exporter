package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.ActivityWithHours
import com.artemkaxboy.redmineexporter.entity.User
import com.artemkaxboy.redmineexporter.repository.TimeEntryRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TimeEntryService(

    private val timeEntryRepository: TimeEntryRepository,
) {

    private val hoursByUsersByActivity = mutableMapOf<Long, List<ActivityWithHours>>()

    fun getMetricByUserIdAndActivityId(userId: Long, activityId: Long): Double {
        return hoursByUsersByActivity[userId]?.find { it.activityId == activityId }?.hours ?: 0.0
    }

    /**
     * Loads metrics from DB for all given users.
     * @param users list of users to load metrics for
     */
    fun fetchMetrics(users: List<User>) {
        users
            .sortedBy { it.id } // for better logs reading
            .forEach {
                fetchMetricsForUser(it)
            }
    }

    private fun fetchMetricsForUser(user: User) {

        val currentMetrics = timeEntryRepository.sumByUserIdAndYearAndMonthGroupedByActivity(
            user.id,
            LocalDate.now().year,
            LocalDate.now().monthValue
        )
        hoursByUsersByActivity[user.id] = currentMetrics
    }
}
