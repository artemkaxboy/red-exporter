package com.artemkaxboy.redmineexporter.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import javax.persistence.Entity
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

    @Test //https://stackoverflow.com/questions/259140/scanning-java-annotations-at-runtime
    fun allAnnotated() {
        val scanner = ClassPathScanningCandidateComponentProvider(true)
//        ClassPathScanningCandidateComponentProvider scanner =
//        new ClassPathScanningCandidateComponentProvider(<DO_YOU_WANT_TO_USE_DEFALT_FILTER>);

        scanner.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))
//        scanner.addIncludeFilter(new AnnotationTypeFilter(<TYPE_YOUR_ANNOTATION_HERE>.class));

        scanner.findCandidateComponents("com.artemkaxboy").forEach {
            println(it)
        }
//        for (BeanDefinition bd : scanner.findCandidateComponents(<TYPE_YOUR_BASE_PACKAGE_HERE>))
//        System.out.println(bd.getBeanClassName());

    }
}
