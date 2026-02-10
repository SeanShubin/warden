package com.seanshubin.warden.domain

import java.nio.file.Path

data class GitStatus(
    val project: Path,
    val hasPendingEdits: Boolean,
    val hasUnpushedCommits: Boolean,
    val statusOutput: String
) {
    val isClean: Boolean get() = !hasPendingEdits && !hasUnpushedCommits

    companion object {
        fun clean(project: Path, statusOutput: String): GitStatus =
            GitStatus(project, false, false, statusOutput)

        fun dirty(project: Path, hasPendingEdits: Boolean, hasUnpushedCommits: Boolean, statusOutput: String): GitStatus =
            GitStatus(project, hasPendingEdits, hasUnpushedCommits, statusOutput)
    }
}
