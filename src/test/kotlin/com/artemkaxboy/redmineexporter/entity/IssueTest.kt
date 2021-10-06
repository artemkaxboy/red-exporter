package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class IssueTest {

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
}
