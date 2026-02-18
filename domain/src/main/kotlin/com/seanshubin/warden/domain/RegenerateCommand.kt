package com.seanshubin.warden.domain

data class RegenerateCommand(
    override val project: Project
) : ProjectCommand {
    override fun exec(environment: CommandEnvironment): CommandResult {
        environment.emitLine("regenerating: ${project.path}")
        val result = environment.buildExecutor.regenerateBuild(
            environment.projectGeneratorPath,
            project
        )
        return if (result.success) {
            CommandResult.Continue()
        } else {
            CommandResult.Stop(ProjectStatus.Status.GenerationFailed(result.output))
        }
    }
}
