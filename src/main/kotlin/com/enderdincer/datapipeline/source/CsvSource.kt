package com.enderdincer.datapipeline.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.io.File

class CsvSource(
    private val filePath: String,
    private val delimiter: String = ",",
    private val skipHeader: Boolean = false
) : Source<List<String>> {

    override fun asFlow(): Flow<List<String>> =
        File(filePath)
            .bufferedReader()
            .lineSequence()
            .skipHeader(skipHeader)
            .asFlow()
            .map { it.split(delimiter) }
}

private fun <T> Sequence<T>.skipHeader(skipHeader: Boolean): Sequence<T> =
    if (skipHeader) drop(1) else this