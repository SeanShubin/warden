package com.seanshubin.warden.composition

import java.nio.file.Path

data class Configuration(
    val baseDir: Path,
    val projectGeneratorPath: Path,
    val codeProjects: List<Path>,
    val gitOnlyProjects: List<Path>
)
