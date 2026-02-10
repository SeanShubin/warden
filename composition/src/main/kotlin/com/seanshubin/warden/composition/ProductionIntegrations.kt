package com.seanshubin.warden.composition

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.di.contract.FilesContract
import com.seanshubin.warden.di.delegate.ExecImpl
import com.seanshubin.warden.di.delegate.FilesDelegate
import java.time.Clock

class ProductionIntegrations(
    override val commandLineArgs: Array<String>
) : Integrations {
    override val clock: Clock = Clock.systemUTC()
    override val emitLine: (String) -> Unit = { println(it) }
    override val files: FilesContract = FilesDelegate.defaultInstance()
    override val exec: Exec = ExecImpl()
}
