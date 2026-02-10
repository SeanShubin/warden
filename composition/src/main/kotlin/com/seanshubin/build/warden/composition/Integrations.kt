package com.seanshubin.build.warden.composition

import com.seanshubin.build.warden.di.contract.FilesContract
import java.time.Clock

interface Integrations {
    val commandLineArgs: Array<String>
    val clock: Clock
    val emitLine: (String) -> Unit
    val files: FilesContract
}
