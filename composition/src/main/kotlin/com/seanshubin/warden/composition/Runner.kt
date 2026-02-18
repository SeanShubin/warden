package com.seanshubin.warden.composition

import com.seanshubin.warden.domain.*
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

        // Create command environment from composition root
        val environment = CommandEnvironmentImpl(
            projectGeneratorPath = configuration.projectGeneratorPath,
            buildExecutor = buildExecutor,
            projectChecker = projectChecker,
            clock = clock,
            emitLine = emitLine,
            formatDuration = DurationFormat.milliseconds::format
        )

        // Create command lists for each valid project
        val commandLists = validProjects.map { project ->
            ProjectCommandFactory.createCommandsFor(project)
        }

        // Execute each command list in parallel (one coroutine per project)
        val results = parallelExecutor.execute(commandLists) { commands ->
            executeCommandSequence(commands, environment)
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

    private fun executeCommandSequence(
        commands: List<ProjectCommand>,
        environment: CommandEnvironment
    ): Triple<Project, ProjectStatus, String> {
        val startMillis = environment.clock.millis()
        var finalStatus: ProjectStatus.Status = ProjectStatus.Status.Clean
        val project = commands.first().project

        for (command in commands) {
            val result = command.exec(environment)
            when (result) {
                is CommandResult.Stop -> {
                    finalStatus = result.status
                    break
                }
                is CommandResult.Continue -> {
                    finalStatus = result.status
                }
            }
        }

        val endMillis = environment.clock.millis()
        val durationMillis = endMillis - startMillis
        val durationText = environment.formatDuration(durationMillis)

        val statusText = when (finalStatus) {
            is ProjectStatus.Status.Clean -> "(ok)"
            is ProjectStatus.Status.GenerationFailed -> "(generation failed)"
            is ProjectStatus.Status.BuildFailed -> "(verify failed)"
            is ProjectStatus.Status.PendingEdits -> "(pending edits)"
            is ProjectStatus.Status.UnpushedCommits -> "(unpushed commits)"
            is ProjectStatus.Status.NoUpstream -> "(no upstream)"
        }
        environment.emitLine("$statusText ${project.path} (time: $durationText)")

        return Triple(project, ProjectStatus(project.path, finalStatus), durationText)
    }
}
