package com.artemkaxboy.redmineexporter.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

const val TIME_ENTRY_ACTIVITY = "TimeEntryActivity"
const val ACTIVE_IS_ACTIVE = 1

@Entity
@Table(name = "enumerations")
class Activity (

    @Id
    val id: Long = -1,

    val name: String,

    val position: Int = id.toInt(),

    val type: String = TIME_ENTRY_ACTIVITY,

    val active: Int,

    @Column(name = "project_id")
    val projectId: Long?,
)
