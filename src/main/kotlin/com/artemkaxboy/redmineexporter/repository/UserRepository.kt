package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByIdIn(ids: List<Long>): List<User>
}
