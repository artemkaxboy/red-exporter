package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
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
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Version

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , project = $project , status = $status )"
    }
}
