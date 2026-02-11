package com.seanshubin.warden.buildexecutor

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.domain.Project
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
