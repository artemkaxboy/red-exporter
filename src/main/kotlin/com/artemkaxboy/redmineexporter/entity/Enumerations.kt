package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import org.jetbrains.annotations.TestOnly
import javax.persistence.*

const val TIME_ENTRY_ACTIVITY = "TimeEntryActivity"
const val ISSUE_PRIORITY = "IssuePriority"

const val ACTIVE_IS_ACTIVE = 1
const val ACTIVE_IS_INACTIVE = 0

@Entity
@Table(name = "enumerations")
class Activity(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @TestOnly
        @JvmOverloads
        fun make(
            id: Long = -1,
            name: String = "Activity Name",
            position: Int = id.toInt(),
            type: String = TIME_ENTRY_ACTIVITY,
            active: Int = ACTIVE_IS_ACTIVE,
            projectId: Long? = null,
        ) =
            Activity(id = id, name = name, position = position, type = type, active = active, projectId = projectId)
    }
}

@Entity
@Table(name = "enumerations")
class Priority(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val name: String,

    val position: Int = id.toInt(),

    val type: String = ISSUE_PRIORITY,

    val active: Int,

    @Column(name = "project_id")
    val projectId: Long?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Priority

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , position = $position , type = $type , active = $active , projectId = $projectId )"
    }

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @TestOnly
        @JvmOverloads
        fun make(
            id: Long = -1,
            name: String = "Priority Name",
            position: Int = id.toInt(),
            type: String = ISSUE_PRIORITY,
            active: Int = ACTIVE_IS_ACTIVE,
            projectId: Long? = null,
        ) =
            Priority(id = id, name = name, position = position, type = type, active = active, projectId = projectId)
    }
}
