package com.seanshubin.warden.composition

class BootstrapDependencies(
    integrations: Integrations
) {
    val bootstrap: Bootstrap = Bootstrap(integrations)
}
