package com.artemkaxboy.redmineexporter.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "issues")
class Issue(

    @Id
    val id: Long = -1,

    @Column(name = "tracker_id")
    val trackerId: Long,

    @Column(name = "project_id")
    val projectId: Long,

    @Column(name = "status_id")
    val statusId: Long,

    @Column(name = "priority_id")
    val priorityId: Long,

    @Column(name = "fixed_version_id")
    val fixedVersionId: Long,

    @Column(name = "author_id")
    val authorId: Long,
)
