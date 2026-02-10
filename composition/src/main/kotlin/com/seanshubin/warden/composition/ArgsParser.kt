package com.seanshubin.warden.composition

object ArgsParser {
    fun parseConfigBaseName(args: Array<String>): String {
        return if (args.isEmpty()) {
            "build-warden"
        } else {
            args[0]
        }
    }
}
