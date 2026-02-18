package com.seanshubin.warden.domain

import java.nio.file.Path
import java.time.Clock

data class CodeProject(
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

        // Regenerate build
        emitLine("regenerating: $path")
        val generationResults = buildExecutor.regenerateBuilds(projectGeneratorPath, listOf(this))
        val generationResult = generationResults[path]

        // Check if generation succeeded
        if (generationResult != null && !generationResult.success) {
            val projectEndMillis = clock.millis()
            val projectDurationMillis = projectEndMillis - projectStartMillis
            val projectDurationText = formatDuration(projectDurationMillis)
            val status = ProjectStatus(path, ProjectStatus.Status.GenerationFailed(generationResult.output))
            emitLine("(generation failed) $path (time: $projectDurationText)")
            return Triple(this, status, projectDurationText)
        }

        // Check status with progress messages
        emitLine("verifying: $path")
        val status = projectChecker.checkProjects(listOf(this)).first()

        // Show what checks were done based on status
        when (status.status) {
            is ProjectStatus.Status.GenerationFailed -> {
                // Generation failed, didn't check build or git
            }
            is ProjectStatus.Status.BuildFailed -> {
                // Build failed, didn't check git
            }
            is ProjectStatus.Status.PendingEdits -> {
                // Build passed, found pending edits
                emitLine("edits?: $path")
            }
            is ProjectStatus.Status.NoUpstream -> {
                // Build passed, no edits, but no upstream configured
                emitLine("edits?: $path")
                emitLine("unpushed?: $path")
            }
            is ProjectStatus.Status.UnpushedCommits -> {
                // Build passed, no edits, found unpushed
                emitLine("edits?: $path")
                emitLine("unpushed?: $path")
            }
            is ProjectStatus.Status.Clean -> {
                // All checks passed
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
