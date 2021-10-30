package com.artemkaxboy.redmineexporter.schedule

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
internal class MetricsUpdaterTest {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun `bean does not exist in test profile`() {

        val beanNames = applicationContext.getBeanNamesForType(MetricsUpdater::class.java)

        Assertions.assertThat(beanNames).hasSize(0)
    }
}

@SpringBootTest(properties = [ "spring.profiles.active=prod" ])
@RunWith(SpringRunner::class)
internal class MetricsUpdaterNonTestProfileTest {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun `bean exists in non test profile`() {

        val beanNames = applicationContext.getBeanNamesForType(MetricsUpdater::class.java)

        Assertions.assertThat(beanNames).hasSize(1)
    }
}
