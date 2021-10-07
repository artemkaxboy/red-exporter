package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import javax.persistence.Entity
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

const val MAKE_FUN_NAME = "make"
const val TO_STRING_FUN_NAME = "toString"

internal class ReflectionTests {

    private fun checkEntity(clazz: KClass<*>) {

        println("Testing entity class: ${clazz.qualifiedName}")
        val instance = clazz.companionObject?.java?.getMethod(MAKE_FUN_NAME)?.invoke(clazz.companionObjectInstance)
        Assertions.assertThat(instance).isNotNull

        val toStringResult = clazz.functions.find { it.name == TO_STRING_FUN_NAME }?.call(instance)?.toString()
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

    @Test
    fun allAnnotated() {
        ClassPathScanningCandidateComponentProvider(false)
            .apply {
                addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
            }
            .findCandidateComponents("com.artemkaxboy").forEach {
                checkEntity(Class.forName(it.beanClassName).kotlin)
            }
    }
}
