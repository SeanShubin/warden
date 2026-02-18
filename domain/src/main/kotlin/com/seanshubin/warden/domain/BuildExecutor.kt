package com.seanshubin.warden.domain

import java.nio.file.Path

interface BuildExecutor {
    data class Result(
        val exitCode: Int,
        val output: String
    ) {
        val success: Boolean get() = exitCode == 0
    }

    fun regenerateBuilds(projectGeneratorPath: Path, projects: List<Project>): Map<Path, Result>
}
