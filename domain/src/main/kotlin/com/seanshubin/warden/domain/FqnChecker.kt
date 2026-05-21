package com.seanshubin.warden.domain

import java.nio.file.Path

interface FqnChecker {
    fun findViolations(projectPath: Path): List<String>
}
