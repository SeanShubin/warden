package com.seanshubin.warden.composition

import com.seanshubin.warden.json.mappers.JsonMappers
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

        val codeProjects = (configMap["codeProjects"] as List<*>)
            .map { baseDir.resolve(it as String) }

        val gitOnlyProjects = (configMap["gitOnlyProjects"] as List<*>)
            .map { baseDir.resolve(it as String) }

        return Configuration(
            baseDir = baseDir,
            projectGeneratorPath = projectGeneratorPath,
            codeProjects = codeProjects,
            gitOnlyProjects = gitOnlyProjects
        )
    }
}
