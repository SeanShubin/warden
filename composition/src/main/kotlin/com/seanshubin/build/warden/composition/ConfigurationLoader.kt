package com.seanshubin.build.warden.composition

import com.seanshubin.build.warden.dynamic.json.JsonMappers
import java.nio.file.Path
import java.nio.file.Paths

class ConfigurationLoader(
    private val integrations: Integrations,
    private val configBaseName: String
) {
    fun load(): Configuration {
        val configFileName = "$configBaseName.json"
        val configPath = Paths.get(configFileName)
        val configText = integrations.files.readString(configPath)
        val configMap = JsonMappers.parse<Map<String, Any>>(configText)

        val baseDir = Paths.get(configMap["baseDir"] as String)
        val projectGeneratorPath = baseDir.resolve(configMap["projectGeneratorPath"] as String)

        @Suppress("UNCHECKED_CAST")
        val projectStrings = configMap["projects"] as List<String>
        val projects = projectStrings.map { baseDir.resolve(it) }

        return Configuration(
            baseDir = baseDir,
            projectGeneratorPath = projectGeneratorPath,
            projects = projects
        )
    }
}
