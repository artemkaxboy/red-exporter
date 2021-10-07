package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import javax.persistence.Entity
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

internal class IssueTest {

    // TODO check all entities with reflection
    @Test
    fun testToString() {

        val versionString = Issue.make().toString()

        Issue::class.memberProperties.forEach {
            when(it.returnType.classifier) {
                Long::class, String::class, Int::class ->
                    Assertions.assertThat(versionString).containsIgnoringCase(it.name)
            }
        }
    }

    @Test
    fun testHashCode() {

        Assertions.assertThat(Issue.make().hashCode()).isEqualTo(0)
    }

    fun checkk(clazz: KClass<*>) {

        println(clazz.simpleName)
        clazz.memberProperties.forEach {
            when(it.returnType.classifier) {
                Long::class, String::class, Int::class ->
                    Assertions.assertThat(it.name).containsIgnoringCase(it.name)
            }
        }
    }

    @Test //https://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
    fun allAnnotated() {
        val scc = ClassPathScanningCandidateComponentProvider(false)
        scc.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
        scc.findCandidateComponents("com.artemkaxboy").forEach {
            checkk(Class.forName(it.beanClassName).kotlin)
        }
    }
}
