package com.seanshubin.warden.projectchecker

import com.seanshubin.warden.domain.FqnChecker
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.domain.ProjectChecker
import com.seanshubin.warden.domain.ProjectStatus
import com.seanshubin.warden.exec.Exec

class ProjectCheckerImpl(
    private val exec: Exec,
    private val fqnChecker: FqnChecker
) : ProjectChecker {
    override fun verifyBuild(project: Project): ProjectStatus {
        val buildResult = exec.execForResult(project.path, listOf("mvn", "clean", "verify"))
        if (!buildResult.success) {
            return ProjectStatus(project.path, ProjectStatus.Status.BuildFailed(buildResult.output))
        }

        val violations = fqnChecker.findViolations(project.path)
        if (violations.isNotEmpty()) {
            return ProjectStatus(project.path, ProjectStatus.Status.FqnViolationsFound(violations.joinToString("\n")))
        }

        return checkGit(project)
    }

    override fun checkGit(project: Project): ProjectStatus {
        // Check pending edits (uncommitted changes)
        val statusResult = exec.execForResult(project.path, listOf("git", "status", "--porcelain"))
        if (statusResult.success && statusResult.output.trim().isNotEmpty()) {
            return ProjectStatus(project.path, ProjectStatus.Status.PendingEdits)
        }

        // Check unpushed commits
        val unpushedResult = exec.execForResult(project.path, listOf("git", "log", "@{u}..HEAD", "--oneline"))
        if (!unpushedResult.success && unpushedResult.output.contains("no upstream configured")) {
            return ProjectStatus(project.path, ProjectStatus.Status.NoUpstream)
        }
        if (unpushedResult.success && unpushedResult.output.trim().isNotEmpty()) {
            return ProjectStatus(project.path, ProjectStatus.Status.UnpushedCommits)
        }

        // All checks passed
        return ProjectStatus(project.path, ProjectStatus.Status.Clean)
    }
}
