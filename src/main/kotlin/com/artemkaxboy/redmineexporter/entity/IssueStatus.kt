package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
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

    val position: Int = id.toInt(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as IssueStatus

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , isClosed = $isClosed , position = $position )"
    }
}
