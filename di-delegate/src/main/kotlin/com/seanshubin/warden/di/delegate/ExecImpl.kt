package com.seanshubin.warden.di.delegate

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.di.contract.ProcessBuilderContract
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

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
