package com.seanshubin.warden.domain

import java.nio.file.Path

abstract class Project {
    abstract val path: Path
    abstract val isValid: Boolean
    abstract val issues: List<String>
}
