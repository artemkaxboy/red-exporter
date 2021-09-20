package com.artemkaxboy.redmineexporter.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "versions")
class Version(

    @Id
    val id: Long = -1,

    val name: String,

    @Column(name = "project_id")
    val projectId: Long,

    val status: String,
)
