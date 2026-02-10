package com.seanshubin.warden.domain

import java.nio.file.Path

data class Project(
    val path: Path,
    val isValid: Boolean,
    val issues: List<String>
) {
    companion object {
        fun valid(path: Path): Project = Project(path, true, emptyList())
        fun invalid(path: Path, vararg issues: String): Project =
            Project(path, false, issues.toList())
    }
}
