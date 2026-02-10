package com.seanshubin.warden.di.contract

interface ParallelExecutor {
    fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R>
}
