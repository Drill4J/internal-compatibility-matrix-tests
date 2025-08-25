package com.epam.drill.compatibility.async

import com.epam.drill.compatibility.matrix.AsyncMatrixTest
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ScheduledExecutorServiceTest : AsyncMatrixTest() {
    private val executor = Executors.newScheduledThreadPool(1);
    init {
        executor.submit(Callable {
            // Initialize a thread of the executor service
        })
    }

    override fun callAsyncCommunication(task: () -> String): Future<String> {
        return executor.schedule(Callable {
            task()
        }, 100, TimeUnit.MILLISECONDS)
    }
}