package com.enderdincer.datapipeline.transformer


fun interface Transformer<I, R> {
    fun transform(input: I): R
}
