package com.seanshubin.warden.composition

import com.seanshubin.warden.domain.BuildExecutor
import com.seanshubin.warden.domain.CommandEnvironment
import com.seanshubin.warden.domain.ProjectChecker
import java.nio.file.Path
import java.time.Clock

data class CommandEnvironmentImpl(
    override val projectGeneratorPath: Path,
    override val buildExecutor: BuildExecutor,
    override val projectChecker: ProjectChecker,
    override val clock: Clock,
    override val emitLine: (String) -> Unit,
    override val formatDuration: (Long) -> String
) : CommandEnvironment
