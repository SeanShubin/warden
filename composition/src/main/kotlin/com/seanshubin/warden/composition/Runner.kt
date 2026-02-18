package com.seanshubin.warden.composition

import com.seanshubin.warden.domain.BuildExecutor
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.domain.ProjectChecker
import com.seanshubin.warden.domain.ProjectStatus
import com.seanshubin.warden.format.DurationFormat
import com.seanshubin.warden.parallel.ParallelExecutor
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

        val codeProjects = projectFinder.findProjects(configuration.codeProjects)
        val gitOnlyProjects = projectFinder.findGitOnlyProjects(configuration.gitOnlyProjects)

        val allProjects = codeProjects + gitOnlyProjects
        val validProjects = allProjects.filter { it.isValid }

        // Show invalid projects immediately
        allProjects.filter { !it.isValid }.forEach { project ->
            emitLine("(invalid) ${project.path}")
            project.issues.forEach { issue ->
                emitLine("  $issue")
            }
        }

        // Process ALL projects in parallel (both types) using polymorphism
        val results = parallelExecutor.execute(validProjects) { project ->
            project.process(
                configuration.projectGeneratorPath,
                buildExecutor,
                projectChecker,
                clock,
                emitLine,
                DurationFormat.milliseconds::format
            )
        }

        // Results summary
        emitLine("")
        emitLine("---------- results ----------")
        results.forEach { (project, status, durationText) ->
            val statusText = when (status.status) {
                is ProjectStatus.Status.Clean -> "(ok)"
                is ProjectStatus.Status.GenerationFailed -> "(generation failed)"
                is ProjectStatus.Status.BuildFailed -> "(verify failed)"
                is ProjectStatus.Status.PendingEdits -> "(pending edits)"
                is ProjectStatus.Status.UnpushedCommits -> "(unpushed commits)"
                is ProjectStatus.Status.NoUpstream -> "(no upstream)"
            }
            emitLine("$statusText ${project.path} (time: $durationText)")
        }

        val endMillis = clock.millis()
        val durationMillis = endMillis - startMillis
        val durationText = DurationFormat.milliseconds.format(durationMillis)
        emitLine("time taken: $durationText")
    }
}
