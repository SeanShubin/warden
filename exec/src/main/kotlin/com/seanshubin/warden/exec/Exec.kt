package com.seanshubin.warden.exec

import java.nio.file.Path

//
// This file was imported from: ../kotlin-reusable
// Module: exec
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

interface Exec {
    data class Result(
        val exitCode: Int,
        val output: String
    ) {
        val success: Boolean get() = exitCode == 0
    }

    fun exec(workingDirectory: Path, command: List<String>): Result
}
