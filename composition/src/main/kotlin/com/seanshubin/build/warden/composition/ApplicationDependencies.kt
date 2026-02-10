package com.seanshubin.build.warden.composition

class ApplicationDependencies(
    integrations: Integrations,
    configuration: Configuration
) {
    private val clock = integrations.clock
    private val emitLine = integrations.emitLine

    val runner: Runnable = Runner(clock, emitLine, configuration)

    companion object {
        fun fromConfiguration(integrations: Integrations, config: Configuration): Runnable {
            return ApplicationDependencies(integrations, config).runner
        }
    }
}
