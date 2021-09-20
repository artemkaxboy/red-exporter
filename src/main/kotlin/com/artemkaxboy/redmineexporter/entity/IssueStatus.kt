package com.artemkaxboy.redmineexporter.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "issue_statuses")
class IssueStatus(

    @Id
    val id: Long = -1,

    val name: String,

    @Column(name = "is_closed")
    val isClosed: Int,

    val position: Int,
)
