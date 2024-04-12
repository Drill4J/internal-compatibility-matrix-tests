package com.epam.drill.test.compatibility

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@SpringBootApplication
@EnableAsync
open class AsyncApplication: AsyncConfigurer {
    override fun getAsyncExecutor(): Executor? {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.initialize()
        return threadPoolTaskExecutor
    }
}

fun main(args: Array<String>) {
    runApplication<AsyncApplication>(*args)
}
