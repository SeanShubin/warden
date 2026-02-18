package com.seanshubin.warden.composition

import com.seanshubin.warden.domain.*

object ProjectCommandFactory {
    fun createCommandsFor(project: Project): List<ProjectCommand> {
        return when (project) {
            is CodeProject -> listOf(
                RegenerateCommand(project),
                VerifyBuildCommand(project),
                CheckGitCommand(project)
            )
            is GitOnlyProject -> listOf(
                CheckGitCommand(project)
            )
            else -> throw IllegalArgumentException("Unknown project type: ${project::class}")
        }
    }
}
