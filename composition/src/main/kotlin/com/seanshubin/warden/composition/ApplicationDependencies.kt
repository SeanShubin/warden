package com.seanshubin.warden.composition

import com.seanshubin.warden.buildexecutor.BuildExecutor
import com.seanshubin.warden.buildexecutor.BuildExecutorImpl
import com.seanshubin.warden.projectchecker.ProjectChecker
import com.seanshubin.warden.projectchecker.ProjectCheckerImpl
import com.seanshubin.warden.projectfinder.ProjectFinder
import com.seanshubin.warden.projectfinder.ProjectFinderImpl

class ApplicationDependencies(
    integrations: Integrations,
    configuration: Configuration
) {
    private val clock = integrations.clock
    private val emitLine = integrations.emitLine
    private val files = integrations.files
    private val exec = integrations.exec

    private val projectFinder: ProjectFinder = ProjectFinderImpl(files)
    private val buildExecutor: BuildExecutor = BuildExecutorImpl(exec)
    private val projectChecker: ProjectChecker = ProjectCheckerImpl(exec)

    val runner: Runnable = Runner(clock, emitLine, configuration, projectFinder, buildExecutor, projectChecker)

    companion object {
        fun fromConfiguration(integrations: Integrations, config: Configuration): Runnable {
            return ApplicationDependencies(integrations, config).runner
        }
    }
}
