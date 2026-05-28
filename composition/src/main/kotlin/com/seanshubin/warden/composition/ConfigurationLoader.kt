package com.seanshubin.warden.composition

import com.seanshubin.warden.dynamic.core.KeyValueStore
import com.seanshubin.warden.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.warden.dynamic.json.loadListOrEmpty
import com.seanshubin.warden.dynamic.json.loadStringOrDefault
import java.nio.file.Paths

class ConfigurationLoader(
    private val integrations: Integrations,
    private val configBaseName: String
) {
    fun load(): Configuration {
        val configFileName = "$configBaseName.json"
        val configPath = Paths.get(configFileName)
        val keyValueStore: KeyValueStore = JsonFileKeyValueStore(integrations.files, configPath)

        val baseDir = Paths.get(keyValueStore.loadStringOrDefault(listOf("baseDir"), ".."))
        val projectGeneratorPath = baseDir.resolve(
            keyValueStore.loadStringOrDefault(
                listOf("projectGeneratorPath"),
                "project-generator/console/target/project-generator-console.jar"
            )
        )
        val codeProjects = keyValueStore.loadListOrEmpty(listOf("codeProjects"))
            .map { baseDir.resolve(it as String) }
        val gitOnlyProjects = keyValueStore.loadListOrEmpty(listOf("gitOnlyProjects"))
            .map { baseDir.resolve(it as String) }

        return Configuration(
            baseDir = baseDir,
            projectGeneratorPath = projectGeneratorPath,
            codeProjects = codeProjects,
            gitOnlyProjects = gitOnlyProjects
        )
    }
}
