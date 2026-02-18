package com.seanshubin.warden.domain

import java.nio.file.Path
import java.time.Clock

data class GitOnlyProject(
    override val path: Path,
    override val isValid: Boolean,
    override val issues: List<String>
) : Project() {
    override fun process(
        projectGeneratorPath: Path,
        buildExecutor: BuildExecutor,
        projectChecker: ProjectChecker,
        clock: Clock,
        emitLine: (String) -> Unit,
        formatDuration: (Long) -> String
    ): Triple<Project, ProjectStatus, String> {
        val projectStartMillis = clock.millis()

        // Check git status only
        emitLine("checking git: $path")
        val status = projectChecker.checkGitOnly(listOf(this)).first()

        // Show what checks were done based on status
        when (status.status) {
            is ProjectStatus.Status.GenerationFailed -> {
                // Should not happen for git-only projects
            }
            is ProjectStatus.Status.BuildFailed -> {
                // Should not happen for git-only projects
            }
            is ProjectStatus.Status.PendingEdits -> {
                emitLine("edits?: $path")
            }
            is ProjectStatus.Status.NoUpstream -> {
                emitLine("edits?: $path")
                emitLine("unpushed?: $path")
            }
            is ProjectStatus.Status.UnpushedCommits -> {
                emitLine("edits?: $path")
                emitLine("unpushed?: $path")
            }
            is ProjectStatus.Status.Clean -> {
                emitLine("edits?: $path")
                emitLine("unpushed?: $path")
            }
        }

        // Show completion status with timing
        val projectEndMillis = clock.millis()
        val projectDurationMillis = projectEndMillis - projectStartMillis
        val projectDurationText = formatDuration(projectDurationMillis)
        val statusText = when (status.status) {
            is ProjectStatus.Status.Clean -> "(ok)"
            is ProjectStatus.Status.GenerationFailed -> "(generation failed)"
            is ProjectStatus.Status.BuildFailed -> "(verify failed)"
            is ProjectStatus.Status.PendingEdits -> "(pending edits)"
            is ProjectStatus.Status.UnpushedCommits -> "(unpushed commits)"
            is ProjectStatus.Status.NoUpstream -> "(no upstream)"
        }
        emitLine("$statusText $path (time: $projectDurationText)")

        return Triple(this, status, projectDurationText)
    }
}
