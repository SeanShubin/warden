package com.seanshubin.warden.composition

import com.seanshubin.warden.buildexecutor.BuildExecutor
import com.seanshubin.warden.projectfinder.ProjectFinder
import java.time.Clock

class Runner(
    private val clock: Clock,
    private val emitLine: (String) -> Unit,
    private val configuration: Configuration,
    private val projectFinder: ProjectFinder,
    private val buildExecutor: BuildExecutor
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
        }
    }
}
