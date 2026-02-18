package com.seanshubin.warden.domain

import java.nio.file.Path
import java.time.Clock

interface CommandEnvironment {
    val projectGeneratorPath: Path
    val buildExecutor: BuildExecutor
    val projectChecker: ProjectChecker
    val clock: Clock
    val emitLine: (String) -> Unit
    val formatDuration: (Long) -> String
}
