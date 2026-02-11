package com.seanshubin.warden.buildexecutor

import com.seanshubin.warden.domain.Project
import java.nio.file.Path

interface BuildExecutor {
    fun regenerateBuilds(projectGeneratorPath: Path, projects: List<Project>)
}
