package com.artemkaxboy.redmineexporter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedmineExporterApplication

fun main(args: Array<String>) {
	runApplication<RedmineExporterApplication>(*args)
}
