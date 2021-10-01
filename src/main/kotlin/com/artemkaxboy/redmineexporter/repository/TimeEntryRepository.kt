package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.ActivityWithHours
import com.artemkaxboy.redmineexporter.entity.TimeEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimeEntryRepository : JpaRepository<TimeEntry, Long> {

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ActivityWithHours(t.activityId, SUM(t.hours)) FROM TimeEntry t WHERE t.userId = :userId AND t.year = :year AND t.month = :month GROUP BY t.activityId")
    fun sumByUserIdAndYearAndMonthGroupedByActivity(userId: Long, year: Int, month: Int): List<ActivityWithHours>
}
