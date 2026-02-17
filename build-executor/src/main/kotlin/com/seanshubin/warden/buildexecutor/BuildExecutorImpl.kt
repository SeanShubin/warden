package com.seanshubin.warden.buildexecutor

import com.seanshubin.warden.domain.BuildExecutor
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.exec.Exec
import java.nio.file.Path

class BuildExecutorImpl(
    private val exec: Exec
) : BuildExecutor {
    override fun regenerateBuilds(projectGeneratorPath: Path, projects: List<Project>) {
        val validProjects = projects.filter { it.isValid }
        validProjects.forEach { project ->
            regenerateBuild(projectGeneratorPath, project.path)
        }
    }

    private fun regenerateBuild(projectGeneratorPath: Path, projectPath: Path) {
        val command = listOf("java", "-jar", projectGeneratorPath.toString())
        exec.exec(projectPath, command)
    }
}
