package com.enderdincer.datapipeline

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import java.util.UUID

interface RateLimit {
    fun waitForRateLimit()

    companion object {
        fun noRateLimit(): RateLimit = NoRateLimit()
        fun of(limit: Int, period: Duration = Duration.ofSeconds(1)): RateLimit =
            DefaultRateLimit(limit, period)
    }
}

class NoRateLimit : RateLimit {
    override fun waitForRateLimit(): Unit = Unit
}

data class DefaultRateLimit(
    val limit: Int,
    val period: Duration = Duration.ofSeconds(1)
) : RateLimit {

    private val rateLimiter = RateLimiter.of(
        UUID.randomUUID().toString(),
        RateLimiterConfig.custom()
            .limitForPeriod(limit)
            .limitRefreshPeriod(period)
            .timeoutDuration(period)
            .build()
    )

    override fun waitForRateLimit() {
        rateLimiter.acquirePermission()
    }
}

fun <T> Flow<T>.rateLimited(rateLimit: RateLimit): Flow<T> =
    this.onEach { rateLimit.waitForRateLimit() }