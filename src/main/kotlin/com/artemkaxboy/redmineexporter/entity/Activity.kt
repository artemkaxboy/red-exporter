package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import javax.persistence.*

const val TIME_ENTRY_ACTIVITY = "TimeEntryActivity"
const val ACTIVE_IS_ACTIVE = 1

@Entity
@Table(name = "enumerations")
class Activity (

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id: Long = -1,

    val name: String,

    val position: Int = id.toInt(),

    val type: String = TIME_ENTRY_ACTIVITY,

    val active: Int,

    @Column(name = "project_id")
    val projectId: Long?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Activity

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , position = $position , type = $type , active = $active , projectId = $projectId )"
    }
}
