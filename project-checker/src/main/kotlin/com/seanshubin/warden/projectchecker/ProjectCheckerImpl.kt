package com.seanshubin.warden.projectchecker

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.domain.ProjectStatus

class ProjectCheckerImpl(
    private val exec: Exec
) : ProjectChecker {
    override fun checkProjects(projects: List<Project>): List<ProjectStatus> {
        val validProjects = projects.filter { it.isValid }
        return validProjects.map { project ->
            checkProject(project)
        }
    }

    private fun checkProject(project: Project): ProjectStatus {
        // Check 1: Build verification (mvn clean verify)
        val buildResult = exec.exec(project.path, listOf("mvn", "clean", "verify"))
        if (!buildResult.success) {
            return ProjectStatus(project.path, ProjectStatus.Status.BuildFailed(buildResult.output))
        }

        // Check 2: Pending edits (uncommitted changes)
        val statusResult = exec.exec(project.path, listOf("git", "status", "--porcelain"))
        if (statusResult.success && statusResult.output.trim().isNotEmpty()) {
            return ProjectStatus(project.path, ProjectStatus.Status.PendingEdits)
        }

        // Check 3: Unpushed commits
        val unpushedResult = exec.exec(project.path, listOf("git", "log", "@{u}..HEAD", "--oneline"))
        if (unpushedResult.success && unpushedResult.output.trim().isNotEmpty()) {
            return ProjectStatus(project.path, ProjectStatus.Status.UnpushedCommits)
        }

        // All checks passed
        return ProjectStatus(project.path, ProjectStatus.Status.Clean)
    }
}
