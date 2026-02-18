package com.seanshubin.warden.domain

data class VerifyBuildCommand(
    override val project: Project
) : ProjectCommand {
    override fun exec(environment: CommandEnvironment): CommandResult {
        environment.emitLine("verifying: ${project.path}")
        val status = environment.projectChecker.verifyBuild(project)
        return when (status.status) {
            is ProjectStatus.Status.BuildFailed -> CommandResult.Stop(status.status)
            else -> CommandResult.Continue()
        }
    }
}
