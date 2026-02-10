package com.seanshubin.build.warden.composition

import com.seanshubin.build.warden.di.contract.FilesContract
import com.seanshubin.build.warden.di.delegate.FilesDelegate
import java.time.Clock

class ProductionIntegrations(
    override val commandLineArgs: Array<String>
) : Integrations {
    override val clock: Clock = Clock.systemUTC()
    override val emitLine: (String) -> Unit = { println(it) }
    override val files: FilesContract = FilesDelegate.defaultInstance()
}
