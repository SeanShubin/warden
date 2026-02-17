package com.seanshubin.warden.projectfinder

import com.seanshubin.warden.domain.Project
import java.nio.file.Path

interface ProjectFinder {
    fun findProjects(projectPaths: List<Path>): List<Project>
    fun findGitOnlyProjects(projectPaths: List<Path>): List<Project>
}
