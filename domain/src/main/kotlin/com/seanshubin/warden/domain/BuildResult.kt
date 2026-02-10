package com.seanshubin.warden.domain

import java.nio.file.Path

data class BuildResult(
    val project: Path,
    val success: Boolean,
    val output: String
) {
    companion object {
        fun success(project: Path, output: String): BuildResult =
            BuildResult(project, true, output)

        fun failure(project: Path, output: String): BuildResult =
            BuildResult(project, false, output)
    }
}
