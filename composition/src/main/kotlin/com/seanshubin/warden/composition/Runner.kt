package com.seanshubin.warden.composition

import java.time.Clock

class Runner(
    private val clock: Clock,
    private val emitLine: (String) -> Unit,
    private val configuration: Configuration
) : Runnable {
    override fun run() {
        emitLine("Build Warden starting at ${clock.instant()}")
        emitLine("Base directory: ${configuration.baseDir}")
        emitLine("Project generator: ${configuration.projectGeneratorPath}")
        emitLine("Projects to monitor:")
        configuration.projects.forEach { project ->
            emitLine("  - $project")
        }
        emitLine("Build Warden would run here (not yet implemented)")
    }
}
