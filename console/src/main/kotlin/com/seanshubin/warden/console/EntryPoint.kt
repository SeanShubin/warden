package com.seanshubin.warden.console

import com.seanshubin.warden.composition.ApplicationDependencies
import com.seanshubin.warden.composition.BootstrapDependencies
import com.seanshubin.warden.composition.Integrations
import com.seanshubin.warden.composition.ProductionIntegrations
import kotlin.system.exitProcess

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val exitCode = execute(args)
        exitProcess(exitCode)
    }

    fun execute(args: Array<String>): Int {
        val integrations: Integrations = ProductionIntegrations(args)

        val bootstrapDeps = BootstrapDependencies(integrations)
        val configuration = bootstrapDeps.bootstrap.loadConfiguration()

        val appDeps = ApplicationDependencies(integrations, configuration)
        appDeps.runner.run()

        return 0
    }
}
