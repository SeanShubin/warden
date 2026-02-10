package com.seanshubin.warden.projectfinder

import com.seanshubin.warden.di.contract.FilesContract
import com.seanshubin.warden.domain.Project
import java.nio.file.Path

class ProjectFinderImpl(
    private val files: FilesContract
) : ProjectFinder {
    override fun findProjects(projectPaths: List<Path>): List<Project> {
        return projectPaths.map { validateProject(it) }
    }

    private fun validateProject(projectPath: Path): Project {
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

        return if (issues.isEmpty()) {
            Project.valid(projectPath)
        } else {
            Project.invalid(projectPath, *issues.toTypedArray())
        }
    }
}
