package com.artemkaxboy.redmineexporter.entity

import javax.persistence.*

@Entity
@Table(name = "versions")
class Version(

    @Id
    val id: Long = -1,

    val name: String,

    @Column(name = "project_id")
    val projectId: Long,

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false, insertable = false)
    val project: Project?,

    val status: String,
)
