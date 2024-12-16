package com.enderdincer.datapipeline

import com.enderdincer.datapipeline.sink.CsvSink
import com.enderdincer.datapipeline.source.CsvSource
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.lang.Exception

class DataPipelineTest {

    @Test
    fun `test pipeline`() = runTest {
        val dataPipeline = DatePipeline(
            source = CsvSource(
                "/Users/enderdincer/projects/data-pipeline/src/test/resources/title.ratings.tsv",
                delimiter = "\t",
                skipHeader = true
            ),
            transformer = { it.joinToString("---") },
            sink = { println(it) },
            readLimit = DefaultRateLimit(10_000),
            writeLimit = DefaultRateLimit(3_500),
        )
        dataPipeline.process()
    }

    @Test
    fun `test pipeline 2`() = runTest {
        val dataPipeline = DatePipeline(
            source = CsvSource(
                "/Users/enderdincer/projects/data-pipeline/src/test/resources/title.ratings.tsv",
                delimiter = "\t",
                skipHeader = true
            ),
            transformer = {
                try {
                    it.map { cell -> "transformed_$cell" }
                } catch (e: Exception) {
                    listOf("1", "2", "3")
                }
            },
            sink = CsvSink(
                "/Users/enderdincer/projects/data-pipeline/src/test/resources/output.csv",
                headers = listOf("__tt_removed__", "score", "voteCount")
            )
        )
        dataPipeline.process()
        println("== Done ==")
    }
}