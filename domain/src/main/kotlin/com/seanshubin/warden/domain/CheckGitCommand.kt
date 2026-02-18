package com.seanshubin.warden.domain

data class CheckGitCommand(
    override val project: Project
) : ProjectCommand {
    override fun exec(environment: CommandEnvironment): CommandResult {
        val status = environment.projectChecker.checkGit(project)

        // Emit progress indicators based on status
        when (status.status) {
            is ProjectStatus.Status.PendingEdits,
            is ProjectStatus.Status.NoUpstream,
            is ProjectStatus.Status.UnpushedCommits,
            is ProjectStatus.Status.Clean -> {
                environment.emitLine("edits?: ${project.path}")
            }
            else -> {
                // Generation/build failures don't check git
            }
        }

        when (status.status) {
            is ProjectStatus.Status.NoUpstream,
            is ProjectStatus.Status.UnpushedCommits,
            is ProjectStatus.Status.Clean -> {
                environment.emitLine("unpushed?: ${project.path}")
            }
            else -> {
                // Generation/build failures or pending edits don't check upstream
            }
        }

        return when (status.status) {
            is ProjectStatus.Status.Clean -> CommandResult.Continue(status.status)
            else -> CommandResult.Stop(status.status)
        }
    }
}
