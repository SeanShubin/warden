package com.seanshubin.warden.projectchecker

import com.seanshubin.warden.domain.FqnChecker
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines

class FqnCheckerImpl : FqnChecker {
    override fun findViolations(projectPath: Path): List<String> {
        return Files.walk(projectPath)
            .filter { it.isRegularFile() }
            .filter { it.extension in sourceExtensions }
            .filter { path -> excludedDirs.none { path.toString().contains("/$it/") } }
            .flatMap { file -> checkFile(projectPath, file).stream() }
            .toList()
    }

    private fun checkFile(projectPath: Path, file: Path): List<String> {
        val relative = projectPath.relativize(file)
        return file.readLines()
            .withIndex()
            .filter { (_, line) -> !skipPattern.containsMatchIn(line) }
            .filter { (_, line) -> !quotedFqnPattern.containsMatchIn(line) }
            .filter { (_, line) -> fqnPattern.containsMatchIn(line) }
            .map { (index, line) -> "$relative:${index + 1}:$line" }
    }

    companion object {
        private val sourceExtensions = setOf("kt", "java", "scala")
        private val excludedDirs = setOf("target", "generated", ".git")
        private val fqnPattern = Regex("""[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+\.[A-Z][a-zA-Z0-9_]*""")
        private val skipPattern = Regex("""^\s*(package |import |//|\*|/\*)""")
        private val quotedFqnPattern = Regex(""""[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+\.[A-Z][a-zA-Z0-9_]*"""")
    }
}
