package com.seanshubin.warden.projectchecker

import com.seanshubin.warden.domain.Project
import com.seanshubin.warden.domain.ProjectStatus

interface ProjectChecker {
    fun checkProjects(projects: List<Project>): List<ProjectStatus>
}
