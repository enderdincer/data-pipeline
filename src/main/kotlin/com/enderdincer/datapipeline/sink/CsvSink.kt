package com.enderdincer.datapipeline.sink

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

class CsvSink(
    private val filePath: String,
    private val delimiter: String = ",",
    private val headers: List<String> = emptyList(),
    private val batchSize: Int = 5000
) : CloseableSink<List<String>> {

    @Volatile
    private var  isHeaderWritten = false
    private val batch = mutableListOf<List<String>>()
    private val mutex = Mutex()

    override suspend fun write(item: List<String>) {
        mutex.withLock {
            if (!isHeaderWritten && headers.isNotEmpty()) {
                batch.add(headers)
                isHeaderWritten = true
            }

            batch.add(item)

            if (batch.size >= batchSize) {
                val currentBatch = batch.toList()
                batch.clear()
                writeBatch(currentBatch)
            }
        }
    }

    override suspend fun close() {
        val remainingBatch = mutex.withLock {
            if (batch.isNotEmpty()) {
                val currentBatch = batch.toList()
                batch.clear()
                currentBatch
            } else {
                null
            }
        }

        if (remainingBatch != null) {
            writeBatch(remainingBatch)
        }
    }

    private suspend fun writeBatch(batch: List<List<String>>) = withContext(Dispatchers.IO) {
        FileWriter(File(filePath), true).use { writer ->
            for (row in batch) {
                writer.append(row.joinToString(delimiter))
                writer.appendLine()
            }
        }
    }
}