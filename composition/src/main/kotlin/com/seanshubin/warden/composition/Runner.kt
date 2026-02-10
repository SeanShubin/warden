package com.seanshubin.warden.composition

import com.seanshubin.warden.buildexecutor.BuildExecutor
import com.seanshubin.warden.di.contract.ParallelExecutor
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.domain.ProjectStatus
import com.seanshubin.warden.format.DurationFormat
import com.seanshubin.warden.projectchecker.ProjectChecker
import com.seanshubin.warden.projectfinder.ProjectFinder
import java.time.Clock

class Runner(
    private val clock: Clock,
    private val emitLine: (String) -> Unit,
    private val configuration: Configuration,
    private val projectFinder: ProjectFinder,
    private val buildExecutor: BuildExecutor,
    private val projectChecker: ProjectChecker,
    private val parallelExecutor: ParallelExecutor
) : Runnable {
    override fun run() {
        val startMillis = clock.millis()

        val projects = projectFinder.findProjects(configuration.projects)
        val validProjects = projects.filter { it.isValid }

        // Show invalid projects immediately
        projects.filter { !it.isValid }.forEach { project ->
            emitLine("(invalid) ${project.path}")
            project.issues.forEach { issue ->
                emitLine("  $issue")
            }
        }

        // Process each valid project with progress messages (in parallel)
        val results = parallelExecutor.execute(validProjects) { project ->
            // Regenerate build
            emitLine("regenerating: ${project.path}")
            buildExecutor.regenerateBuilds(configuration.projectGeneratorPath, listOf(project))

            // Check status with progress messages
            emitLine("verifying: ${project.path}")
            val status = projectChecker.checkProjects(listOf(project)).first()

            // Show what checks were done based on status
            when (status.status) {
                is ProjectStatus.Status.BuildFailed -> {
                    // Build failed, didn't check git
                }
                is ProjectStatus.Status.PendingEdits -> {
                    // Build passed, found pending edits
                    emitLine("edits?: ${project.path}")
                }
                is ProjectStatus.Status.UnpushedCommits -> {
                    // Build passed, no edits, found unpushed
                    emitLine("edits?: ${project.path}")
                    emitLine("unpushed?: ${project.path}")
                }
                is ProjectStatus.Status.Clean -> {
                    // All checks passed
                    emitLine("edits?: ${project.path}")
                    emitLine("unpushed?: ${project.path}")
                }
            }

            // Show completion status
            val statusText = when (status.status) {
                is ProjectStatus.Status.Clean -> "(ok)"
                is ProjectStatus.Status.BuildFailed -> "(verify failed)"
                is ProjectStatus.Status.PendingEdits -> "(pending edits)"
                is ProjectStatus.Status.UnpushedCommits -> "(unpushed commits)"
            }
            emitLine("$statusText ${project.path}")

            project to status
        }

        // Results summary
        emitLine("")
        emitLine("---------- results ----------")
        results.forEach { (project, status) ->
            val statusText = when (status.status) {
                is ProjectStatus.Status.Clean -> "(ok)"
                is ProjectStatus.Status.BuildFailed -> "(verify failed)"
                is ProjectStatus.Status.PendingEdits -> "(pending edits)"
                is ProjectStatus.Status.UnpushedCommits -> "(unpushed commits)"
            }
            emitLine("$statusText ${project.path}")
        }

        val endMillis = clock.millis()
        val durationMillis = endMillis - startMillis
        val durationText = DurationFormat.milliseconds.format(durationMillis)
        emitLine("time taken: $durationText")
    }
}
