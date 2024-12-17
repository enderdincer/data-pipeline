package com.enderdincer.datapipeline
//
//import com.enderdincer.datapipeline.sink.CsvSink
//import com.enderdincer.datapipeline.source.CsvSource
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.Test
//
//class DataPipelineTest {
//
//    @Test
//    fun `test pipeline`() = runTest {
//        val sourceCsvFilePath = "/Users/enderdincer/projects/data-pipeline/src/test/resources/test.csv"
//        TestHelper.createCsvFile(filePath = sourceCsvFilePath, rowCount = 1_000_000L)
//
//        val dataPipeline = DatePipeline(
//            source = CsvSource(sourceCsvFilePath),
//            transformer = {
//                try {
//                    it.map { cell -> "transformed_$cell" }
//                } catch (e: Exception) {
//                    listOf("1", "2", "3")
//                }
//            },
//            sink = CsvSink(
//                "/Users/enderdincer/projects/data-pipeline/src/test/resources/output.csv",
//                headers = listOf("column1", "column2", "column3")
//            )
//        )
//
//        dataPipeline.process()
//
//
//
//        TestHelper.deleteFile(sourceCsvFilePath)
//    }
//
//    @Test
//    fun `test pipeline 2`() = runTest {
//        val dataPipeline = DatePipeline(
//            source = CsvSource(
//                "/Users/enderdincer/projects/data-pipeline/src/test/resources/title.ratings.tsv",
//                delimiter = "\t",
//                skipHeader = true
//            ),
//            transformer = {
//                try {
//                    it.map { cell -> "transformed_$cell" }
//                } catch (e: Exception) {
//                    listOf("1", "2", "3")
//                }
//            },
//            sink = CsvSink(
//                "/Users/enderdincer/projects/data-pipeline/src/test/resources/output.csv",
//                headers = listOf("__tt_removed__", "score", "voteCount")
//            )
//        )
//        dataPipeline.process()
//        println("== Done ==")
//    }
//}