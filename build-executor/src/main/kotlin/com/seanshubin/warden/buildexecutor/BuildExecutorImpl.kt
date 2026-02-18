package com.seanshubin.warden.buildexecutor

import com.seanshubin.warden.domain.BuildExecutor
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.exec.Exec
import java.nio.file.Path

class BuildExecutorImpl(
    private val exec: Exec
) : BuildExecutor {
    override fun regenerateBuild(projectGeneratorPath: Path, project: Project): BuildExecutor.Result {
        val command = listOf("java", "-jar", projectGeneratorPath.toString())
        val execResult = exec.execForResult(project.path, command)
        return BuildExecutor.Result(execResult.exitCode, execResult.output)
    }
}
