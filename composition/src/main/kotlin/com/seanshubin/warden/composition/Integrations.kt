package com.seanshubin.warden.composition

import com.seanshubin.warden.di.contract.Exec
import com.seanshubin.warden.di.contract.FilesContract
import com.seanshubin.warden.di.contract.ParallelExecutor
import java.time.Clock

interface Integrations {
    val commandLineArgs: Array<String>
    val clock: Clock
    val emitLine: (String) -> Unit
    val files: FilesContract
    val exec: Exec
    val parallelExecutor: ParallelExecutor
}
