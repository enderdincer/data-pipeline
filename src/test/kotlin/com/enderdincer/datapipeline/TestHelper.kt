package com.enderdincer.datapipeline

import java.io.File
import java.io.FileWriter

object TestHelper {

    fun createCsvFile(
        filePath: String,
        colCount: Long = 3,
        rowCount: Long = 100_000,
        delimiter: String = ",",
        lineSeparator: String = "\n",
        isWithHeader: Boolean = true,
        cellLength: Int = 5
    ) {
        FileWriter(filePath).use { writer ->
            if (isWithHeader) {
                writer.append(createHeaders(colCount).joinToString(delimiter))
                writer.append(lineSeparator)
            }

            (1..rowCount).onEach {
                writer.append((1..colCount).joinToString(delimiter) { "${randString(cellLength)}-$it" })
                writer.append(lineSeparator)
            }
        }
    }


    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private fun randString(length: Int): String =
        charPool.shuffled().subList(0, length).joinToString("")

    private fun createHeaders(colCount: Long): List<String> = (1..colCount).map { "col$it" }

    fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}