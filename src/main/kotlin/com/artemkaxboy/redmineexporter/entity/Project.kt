package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import org.jetbrains.annotations.TestOnly
import javax.persistence.*

@Entity
@Table(name = "projects")
class Project(

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id: Long = -1,

    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Project

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @TestOnly
        @JvmOverloads
        fun make(id: Long = 1, name: String = "Project Name") = Project(id = id, name = name)
    }
}
