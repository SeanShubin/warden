package com.seanshubin.warden.projectfinder

import com.seanshubin.warden.di.contract.FilesContract
import com.seanshubin.warden.domain.Project
import java.nio.file.Path

class ProjectFinderImpl(
    private val files: FilesContract,
    private val createCodeProject: (Path, Boolean, List<String>) -> Project,
    private val createGitOnlyProject: (Path, Boolean, List<String>) -> Project
) : ProjectFinder {
    override fun findProjects(projectPaths: List<Path>): List<Project> {
        return projectPaths.map { validateCodeProject(it) }
    }

    override fun findGitOnlyProjects(projectPaths: List<Path>): List<Project> {
        return projectPaths.map { validateGitOnlyProject(it) }
    }

    private fun validateCodeProject(projectPath: Path): Project {
        val issues = mutableListOf<String>()

        if (!files.exists(projectPath)) {
            issues.add("Path does not exist: $projectPath")
        } else if (!files.isDirectory(projectPath)) {
            issues.add("Path is not a directory: $projectPath")
        } else {
            val specFile = projectPath.resolve("project-specification.json")
            if (!files.exists(specFile)) {
                issues.add("Missing project-specification.json")
            }
        }

        return createCodeProject(projectPath, issues.isEmpty(), issues)
    }

    private fun validateGitOnlyProject(projectPath: Path): Project {
        val issues = mutableListOf<String>()

        if (!files.exists(projectPath)) {
            issues.add("Path does not exist: $projectPath")
        } else if (!files.isDirectory(projectPath)) {
            issues.add("Path is not a directory: $projectPath")
        }

        return createGitOnlyProject(projectPath, issues.isEmpty(), issues)
    }
}
