package com.seanshubin.warden.domain

import java.nio.file.Path
import java.time.Clock

abstract class Project {
    abstract val path: Path
    abstract val isValid: Boolean
    abstract val issues: List<String>

    abstract fun process(
        projectGeneratorPath: Path,
        buildExecutor: BuildExecutor,
        projectChecker: ProjectChecker,
        clock: Clock,
        emitLine: (String) -> Unit,
        formatDuration: (Long) -> String
    ): Triple<Project, ProjectStatus, String>
}
