package com.seanshubin.warden.domain

import java.nio.file.Path

interface BuildExecutor {
    fun regenerateBuilds(projectGeneratorPath: Path, projects: List<Project>)
}
