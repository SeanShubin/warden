package com.seanshubin.build.warden.composition

import java.nio.file.Path

data class Configuration(
    val baseDir: Path,
    val projectGeneratorPath: Path,
    val projects: List<Path>
)
