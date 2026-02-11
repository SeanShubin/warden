package com.seanshubin.warden.exec

import com.seanshubin.warden.di.contract.ProcessBuilderContract
import com.seanshubin.warden.di.delegate.ProcessBuilderDelegate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

//
// This file was imported from: ../kotlin-reusable
// Module: exec
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class ExecImpl : Exec {
    override fun exec(workingDirectory: Path, command: List<String>): Exec.Result {
        val processBuilder: ProcessBuilderContract = ProcessBuilderDelegate(command)
        processBuilder.directory(workingDirectory.toFile())
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.readText()
        }
        val exitCode = process.waitFor()

        return Exec.Result(exitCode, output)
    }
}
