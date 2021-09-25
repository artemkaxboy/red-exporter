package com.artemkaxboy.redmineexporter.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "projects")
class Project(

    @Id
    val id: Long = -1,

    val name: String,
) {

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }
}
