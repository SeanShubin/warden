package com.seanshubin.warden.domain

import java.nio.file.Path

data class GitOnlyProject(
    override val path: Path,
    override val isValid: Boolean,
    override val issues: List<String>
) : Project()
