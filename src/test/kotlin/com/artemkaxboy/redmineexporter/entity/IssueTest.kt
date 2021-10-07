package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import javax.persistence.Entity
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

internal class IssueTest {

    // TODO check all entities with reflection
    @Test
    fun testToString() {

        val versionString = IssueStatus.make().toString()

        Issue::class.memberProperties.forEach {
            when (it.returnType.classifier) {
                Long::class, String::class, Int::class ->
                    Assertions.assertThat(versionString).containsIgnoringCase(it.name)
            }
        }
    }

    @Test
    fun testHashCode() {

        Assertions.assertThat(IssueStatus.make().hashCode()).isEqualTo(0)
    }

    private fun checkEntity(clazz: KClass<*>) {

        assertDoesNotThrow {

            println("Testing class: ${clazz.simpleName}")
            val instance = clazz.companionObject?.java?.getMethod("make")?.invoke(clazz.companionObjectInstance)
            Assertions.assertThat(instance).isNotNull

            val toStringResult = clazz.functions.find { it.name == "toString" }?.call(instance)?.toString()
            Assertions.assertThat(toStringResult).isNotNull

            clazz.memberProperties.forEach {
                when (it.returnType.classifier) {
                    Long::class, String::class, Int::class, Double::class, Float::class, Char::class ->
                        Assertions.assertThat(toStringResult).containsIgnoringCase(it.name)
                    else ->
                        println("Skipped: ${clazz.simpleName}.${it.name}: [${it.returnType.classifier}]")
                }
            }

            Assertions.assertThat(instance).hasSameHashCodeAs(0)

        }

    }

    @Test //https://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
    fun allAnnotated() {
        val scc = ClassPathScanningCandidateComponentProvider(false)
        scc.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
        scc.findCandidateComponents("com.artemkaxboy").forEach {
            checkEntity(Class.forName(it.beanClassName).kotlin)
        }
    }
}
