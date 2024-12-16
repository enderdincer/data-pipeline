package com.enderdincer.datapipeline.source

import kotlinx.coroutines.flow.Flow

fun interface Source<T> {
    fun asFlow(): Flow<T>
}