package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueCategory
import com.artemkaxboy.redmineexporter.repository.IssueCategoryRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class IssueCategoryServiceTest {

    @Autowired
    lateinit var issueCategoryRepository: IssueCategoryRepository

    @Autowired
    lateinit var issueCategoryService: IssueCategoryService

    private val unknownProject1Id = -1L
    private val project1Id = 1L
    private val project2Id = 2L

    val testCategories = listOf(
        IssueCategory.make(projectId = project1Id, name = "Backend"),
        IssueCategory.make(projectId = project2Id, name = "Frontend"),
        IssueCategory.make(projectId = project2Id, name = "DB"),
    )

    @AfterEach
    fun clearIssues() {
        issueCategoryRepository.deleteAllInBatch()
        issueCategoryService.reset()
    }

    @Test
    fun `returns no categories before fetch`() {

        issueCategoryRepository.saveAll(testCategories)

        val got = issueCategoryService.getAllCategories(project1Id)
        Assertions.assertThat(got).isEmpty()
    }

    @Test
    fun `returns all categories for known project after fetch`() {

        val expected = issueCategoryRepository.saveAll(testCategories)
        issueCategoryService.fetchCategories()

        val got = issueCategoryService.getAllCategories(project1Id)
        Assertions.assertThat(got).hasSameElementsAs(expected.filter { it.projectId == project1Id })
    }

    @Test
    fun `returns no categories for unknown project after fetch`() {

        val expected = issueCategoryRepository.saveAll(testCategories)
        issueCategoryService.fetchCategories()

        val got = issueCategoryService.getAllCategories(unknownProject1Id)
        Assertions.assertThat(got).hasSameElementsAs(expected.filter { it.projectId == unknownProject1Id })
    }

    @Test
    fun `clears all categories by reset`() {

        issueCategoryRepository.saveAll(testCategories)
        issueCategoryService.fetchCategories()
        issueCategoryService.reset()

        val got = issueCategoryService.getAllCategories(project1Id)
        Assertions.assertThat(got).isEmpty()
    }
}
