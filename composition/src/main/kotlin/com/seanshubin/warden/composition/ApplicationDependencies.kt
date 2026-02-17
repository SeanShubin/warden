package com.seanshubin.warden.composition

import com.seanshubin.warden.buildexecutor.BuildExecutorImpl
import com.seanshubin.warden.domain.BuildExecutor
import com.seanshubin.warden.domain.CodeProject
import com.seanshubin.warden.domain.GitOnlyProject
import com.seanshubin.warden.domain.ProjectChecker
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
    private val parallelExecutor = integrations.parallelExecutor

    private val projectFinder: ProjectFinder = ProjectFinderImpl(
        files,
        createCodeProject = { path, isValid, issues -> CodeProject(path, isValid, issues) },
        createGitOnlyProject = { path, isValid, issues -> GitOnlyProject(path, isValid, issues) }
    )
    private val buildExecutor: BuildExecutor = BuildExecutorImpl(exec)
    private val projectChecker: ProjectChecker = ProjectCheckerImpl(exec)

    val runner: Runnable = Runner(clock, emitLine, configuration, projectFinder, buildExecutor, projectChecker, parallelExecutor)

    companion object {
        fun fromConfiguration(integrations: Integrations, config: Configuration): Runnable {
            return ApplicationDependencies(integrations, config).runner
        }
    }
}
