package com.epam.drill.test.compatibility

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@Service
open class SimpleAsyncService {
    @Async
    open fun doAsyncTask(task: () -> String): Future<String> {
        val result = task()
        return CompletableFuture.completedFuture(result)
    }
}