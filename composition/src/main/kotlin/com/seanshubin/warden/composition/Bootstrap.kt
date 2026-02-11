package com.seanshubin.warden.composition

class Bootstrap(
    private val integrations: Integrations
) {
    private val argsParser = ArgsParser

    fun loadConfiguration(): Configuration {
        val configBaseName = argsParser.parseConfigBaseName(integrations.commandLineArgs)
        val loader = ConfigurationLoader(integrations, configBaseName)
        return loader.load()
    }
}
