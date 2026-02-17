package com.seanshubin.warden.domain

interface ProjectChecker {
    fun checkProjects(projects: List<Project>): List<ProjectStatus>
    fun checkGitOnly(projects: List<Project>): List<ProjectStatus>
}
