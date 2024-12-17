package com.enderdincer.datapipeline

import com.enderdincer.datapipeline.sink.CloseableSink
import com.enderdincer.datapipeline.sink.Sink
import com.enderdincer.datapipeline.source.Source
import com.enderdincer.datapipeline.transformer.Transformer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataPipeline<SOURCE, SINK>(
    private val source: Source<SOURCE>,
    private val transformer: Transformer<SOURCE, SINK>,
    private val sink: Sink<SINK>,
    private val readLimit: RateLimit = RateLimit.noRateLimit(),
    private val writeLimit: RateLimit = RateLimit.noRateLimit(),
    private val readConcurrency: Int = 20,
    private val writeConcurrency: Int = 20,
) {

    suspend fun process() = runBlocking {
        val channel = Channel<SINK>(capacity = 10_000)

        val readJob = launch {
            source.asFlow()
                .rateLimited(readLimit)
                .concurrentMap(readConcurrency) { transformer.transform(it) }
                .collect { channel.send(it) }
            channel.close()
        }

        val writeJob = launch {
            channel.consumeAsFlow()
                .rateLimited(writeLimit)
                .concurrentMap(writeConcurrency) { sink.write(it) }
                .collect()
        }

        readJob.join()
        writeJob.join()
        if (sink is CloseableSink) sink.close()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T>.concurrentMap(concurrency: Int, transform: suspend (T) -> R): Flow<R> =
    this.flatMapMerge(concurrency) { item -> flow { emit(transform(item)) } }