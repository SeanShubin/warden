package com.seanshubin.warden.di.contract

import java.nio.file.Path

interface Exec {
    data class Result(
        val exitCode: Int,
        val output: String
    ) {
        val success: Boolean get() = exitCode == 0
    }

    fun exec(workingDirectory: Path, command: List<String>): Result
}
