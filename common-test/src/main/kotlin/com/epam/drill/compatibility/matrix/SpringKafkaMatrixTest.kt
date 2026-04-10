/**
 * Copyright 2020 - 2022 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.drill.compatibility.matrix

import com.epam.drill.compatibility.context.DRILL_SESSION_ID
import com.epam.drill.compatibility.context.DRILL_TEST_ID
import com.epam.drill.compatibility.context.DrillTestContext
import com.epam.drill.compatibility.context.TEST_CONTEXT_NONE
import com.epam.drill.compatibility.testframeworks.isTestCoveredCode
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("FunctionName")
abstract class SpringKafkaMatrixTest {

    private val agentInstanceId: String? = System.getenv("DRILL_INSTANCE_ID")

    @Test
    open fun `test sending and receiving kafka message with drill context - producer and consumer are covered under test session`() =
        withKafkaBroker { bootstrapServers ->
            val testId = "test-data"
            val testSessionId= "session-123"
            DrillTestContext().store(mapOf(DRILL_SESSION_ID to testSessionId, DRILL_TEST_ID to testId))
            try {
                sendAndReceive(bootstrapServers)
                assertTrue(isTestCoveredCode(agentInstanceId, testId, getProducerSignature()))
                assertTrue(isTestCoveredCode(agentInstanceId, testId, getConsumerSignature()))
            } finally {
                DrillTestContext().remove()
            }
        }

    @Test
    open fun `test sending and receiving kafka message without drill context - producer and consumer are covered with no test session`() =
        withKafkaBroker { bootstrapServers ->
            DrillTestContext().remove()
            sendAndReceive(bootstrapServers)
            assertTrue(isTestCoveredCode(agentInstanceId, TEST_CONTEXT_NONE, getProducerSignature()))
            assertTrue(isTestCoveredCode(agentInstanceId, TEST_CONTEXT_NONE, getConsumerSignature()))
        }

    abstract fun getProducerClass(): Class<*>
    abstract fun getConsumerClass(): Class<*>
    open fun getMethodUnderTest(): String = ""

    fun getProducerSignature(): String = "${getProducerClass().name.replace(".", "/")}:${getMethodUnderTest()}"
    fun getConsumerSignature(): String = "${getConsumerClass().name.replace(".", "/")}:${getMethodUnderTest()}"

    protected abstract fun withKafkaBroker(block: (String) -> Unit)
    protected abstract fun produceMessage(template: KafkaTemplate<String, String>, topic: String, message: String)
    protected abstract fun getListener(body: ((ConsumerRecord<String, String>) -> Unit)): MessageListener<String, String>

    fun sendAndReceive(bootstrapServers: String) {
        val topic = "drill-test-${UUID.randomUUID()}"

        val producerFactory = DefaultKafkaProducerFactory<String, String>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            )
        )
        try {
            produceMessage(KafkaTemplate(producerFactory), topic, "test-value")
        } finally {
            producerFactory.destroy()
        }

        val latch = CountDownLatch(1)
        val consumer = getListener {
            latch.countDown()
        }
        val containerProps = ContainerProperties(topic).also { it.messageListener = consumer }
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "drill-test-group-${UUID.randomUUID()}",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            )
        )
        val container = KafkaMessageListenerContainer(consumerFactory, containerProps)
        container.start()
        try {
            latch.await(10, TimeUnit.SECONDS)
        } finally {
            container.stop()
        }
    }
}
