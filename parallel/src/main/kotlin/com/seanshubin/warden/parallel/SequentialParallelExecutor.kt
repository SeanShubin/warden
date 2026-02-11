package com.seanshubin.warden.parallel

//
// This file was imported from: ../kotlin-reusable
// Module: parallel
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class SequentialParallelExecutor : ParallelExecutor {
    override fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R> {
        return items.map(operation)
    }
}
