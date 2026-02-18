package com.seanshubin.warden.domain

interface ProjectCommand {
    val project: Project
    fun exec(environment: CommandEnvironment): CommandResult
}

sealed class CommandResult {
    data class Continue(val status: ProjectStatus.Status = ProjectStatus.Status.Clean) : CommandResult()
    data class Stop(val status: ProjectStatus.Status) : CommandResult()
}
