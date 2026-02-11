package com.seanshubin.warden.parallel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

//
// This file was imported from: ../kotlin-reusable
// Module: parallel
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class ConcurrentParallelExecutor : ParallelExecutor {
    override fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R> {
        return runBlocking(Dispatchers.Default) {
            items.map { item ->
                async {
                    operation(item)
                }
            }.awaitAll()
        }
    }
}
