package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class IssueTest {

    // TODO check all entities with reflection
    @Test
    fun testToString() {

        val versionString = makeIssue().toString()

        Issue::class.memberProperties.forEach {
            when(it.returnType.classifier) {
                Long::class, String::class, Int::class ->
                    Assertions.assertThat(versionString).containsIgnoringCase(it.name)
            }
        }
    }

    @Test
    fun testHashCode() {

        Assertions.assertThat(makeIssue().hashCode()).isEqualTo(0)
    }
}
