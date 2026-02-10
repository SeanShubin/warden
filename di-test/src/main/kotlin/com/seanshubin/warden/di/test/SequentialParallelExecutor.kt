package com.seanshubin.warden.di.test

import com.seanshubin.warden.di.contract.ParallelExecutor

class SequentialParallelExecutor : ParallelExecutor {
    override fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R> {
        return items.map(operation)
    }
}
