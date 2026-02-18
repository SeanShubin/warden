package com.seanshubin.warden.domain

interface ProjectChecker {
    fun verifyBuild(project: Project): ProjectStatus
    fun checkGit(project: Project): ProjectStatus
}
