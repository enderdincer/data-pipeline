package com.enderdincer.datapipeline.sink


fun interface Sink<T> {
    suspend fun write(item: T)
}

interface CloseableSink<T> : Sink<T> {
    suspend fun close()
}