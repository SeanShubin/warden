package com.seanshubin.warden.domain

import java.nio.file.Path

data class CodeProject(
    override val path: Path,
    override val isValid: Boolean,
    override val issues: List<String>
) : Project()
