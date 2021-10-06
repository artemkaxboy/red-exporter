package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class VersionTest {

    @Test
    fun testToString() {

        val versionString = makeVersion().toString()

        Version::class.memberProperties.forEach {
            when(it.returnType.classifier) {
                Long::class, String::class, Int::class ->
                    Assertions.assertThat(versionString).containsIgnoringCase(it.name)
            }
        }
    }
}
