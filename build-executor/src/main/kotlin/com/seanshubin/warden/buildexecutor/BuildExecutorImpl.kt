package com.seanshubin.warden.buildexecutor

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.domain.BuildResult
import com.seanshubin.warden.domain.Project
import java.nio.file.Path

class BuildExecutorImpl(
    private val exec: Exec
) : BuildExecutor {
    override fun regenerateBuilds(projectGeneratorPath: Path, projects: List<Project>): List<BuildResult> {
        val validProjects = projects.filter { it.isValid }
        return validProjects.map { project ->
            regenerateBuild(projectGeneratorPath, project.path)
        }
    }

    private fun regenerateBuild(projectGeneratorPath: Path, projectPath: Path): BuildResult {
        val command = listOf("java", "-jar", projectGeneratorPath.toString())
        val result = exec.exec(projectPath, command)

        return if (result.success) {
            BuildResult.success(projectPath, result.output)
        } else {
            BuildResult.failure(projectPath, result.output)
        }
    }
}
