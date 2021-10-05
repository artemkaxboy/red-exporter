package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "time_entries")
class TimeEntry(

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "user_id")
    val userId: Long,

    val hours: Double,

    @Column(name = "activity_id")
    val activityId: Long,

    @Column(name = "tyear")
    val year: Int,

    @Column(name = "tmonth")
    val month: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TimeEntry

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , userId = $userId , hours = $hours , activityId = $activityId , year = $year , month = $month )"
    }
}
