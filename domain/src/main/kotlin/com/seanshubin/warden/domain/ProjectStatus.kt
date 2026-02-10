package com.seanshubin.warden.domain

import java.nio.file.Path

data class ProjectStatus(
    val project: Path,
    val status: Status
) {
    sealed class Status {
        data object Clean : Status()
        data class BuildFailed(val output: String) : Status()
        data object PendingEdits : Status()
        data object UnpushedCommits : Status()
        data object NoUpstream : Status()
    }

    val isClean: Boolean get() = status is Status.Clean
}
