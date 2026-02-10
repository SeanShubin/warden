package com.seanshubin.warden.composition

import com.seanshubin.warden.buildexecutor.BuildExecutor
import com.seanshubin.warden.domain.ProjectStatus
import com.seanshubin.warden.projectchecker.ProjectChecker
import com.seanshubin.warden.projectfinder.ProjectFinder
import java.time.Clock

class Runner(
    private val clock: Clock,
    private val emitLine: (String) -> Unit,
    private val configuration: Configuration,
    private val projectFinder: ProjectFinder,
    private val buildExecutor: BuildExecutor,
    private val projectChecker: ProjectChecker
) : Runnable {
    override fun run() {
        emitLine("Warden starting at ${clock.instant()}")
        emitLine("Base directory: ${configuration.baseDir}")
        emitLine("Project generator: ${configuration.projectGeneratorPath}")
        emitLine("")

        val projects = projectFinder.findProjects(configuration.projects)
        val validProjects = projects.filter { it.isValid }
        val invalidProjects = projects.filter { !it.isValid }

        emitLine("Projects found: ${projects.size}")
        emitLine("Valid: ${validProjects.size}, Invalid: ${invalidProjects.size}")
        emitLine("")

        if (validProjects.isNotEmpty()) {
            emitLine("Valid projects:")
            validProjects.forEach { project ->
                emitLine("  ✓ ${project.path}")
            }
            emitLine("")
        }

        if (invalidProjects.isNotEmpty()) {
            emitLine("Invalid projects:")
            invalidProjects.forEach { project ->
                emitLine("  ✗ ${project.path}")
                project.issues.forEach { issue ->
                    emitLine("    - $issue")
                }
            }
            emitLine("")
        }

        emitLine("Project finding complete")
        emitLine("")

        if (validProjects.isNotEmpty()) {
            emitLine("Regenerating builds...")
            val buildResults = buildExecutor.regenerateBuilds(configuration.projectGeneratorPath, projects)
            val successfulBuilds = buildResults.filter { it.success }
            val failedBuilds = buildResults.filter { !it.success }

            emitLine("Builds regenerated: ${buildResults.size}")
            emitLine("Successful: ${successfulBuilds.size}, Failed: ${failedBuilds.size}")
            emitLine("")

            if (successfulBuilds.isNotEmpty()) {
                emitLine("Successful builds:")
                successfulBuilds.forEach { result ->
                    emitLine("  ✓ ${result.project}")
                }
                emitLine("")
            }

            if (failedBuilds.isNotEmpty()) {
                emitLine("Failed builds:")
                failedBuilds.forEach { result ->
                    emitLine("  ✗ ${result.project}")
                    emitLine("    ${result.output.lines().firstOrNull() ?: "Unknown error"}")
                }
                emitLine("")
            }

            emitLine("Build regeneration complete")
            emitLine("")
        }

        if (validProjects.isNotEmpty()) {
            emitLine("Checking project status (first error only)...")
            val projectStatuses = projectChecker.checkProjects(projects)
            val cleanProjects = projectStatuses.filter { it.isClean }
            val projectsWithErrors = projectStatuses.filter { !it.isClean }

            emitLine("Projects checked: ${projectStatuses.size}")
            emitLine("Clean: ${cleanProjects.size}, With errors: ${projectsWithErrors.size}")
            emitLine("")

            if (cleanProjects.isNotEmpty()) {
                emitLine("Clean projects:")
                cleanProjects.forEach { status ->
                    emitLine("  ✓ ${status.project}")
                }
                emitLine("")
            }

            if (projectsWithErrors.isNotEmpty()) {
                emitLine("Projects with errors (showing first error only):")
                projectsWithErrors.forEach { status ->
                    emitLine("  ✗ ${status.project}")
                    when (val statusType = status.status) {
                        is ProjectStatus.Status.BuildFailed -> {
                            emitLine("    Build verification failed (mvn clean verify)")
                            val firstLine = statusType.output.lines().firstOrNull() ?: "Unknown error"
                            emitLine("    $firstLine")
                        }
                        is ProjectStatus.Status.PendingEdits -> {
                            emitLine("    Has uncommitted changes")
                        }
                        is ProjectStatus.Status.UnpushedCommits -> {
                            emitLine("    Has unpushed commits")
                        }
                        is ProjectStatus.Status.Clean -> {
                            // Should not happen in this branch
                        }
                    }
                }
                emitLine("")
            }

            emitLine("Project status check complete")
        }
    }
}
