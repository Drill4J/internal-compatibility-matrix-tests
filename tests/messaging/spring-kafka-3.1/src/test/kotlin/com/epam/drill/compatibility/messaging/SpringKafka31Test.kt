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
package com.epam.drill.compatibility.messaging

import com.epam.drill.compatibility.apps.SpringKafkaConsumer
import com.epam.drill.compatibility.apps.SpringKafkaProducer
import com.epam.drill.compatibility.matrix.SpringKafkaMatrixTest
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.ClassRule
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.MessageListener
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class SpringKafka31Test : SpringKafkaMatrixTest() {

    companion object {
        @ClassRule
        @JvmField
        val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"))
    }

    override fun withKafkaBroker(block: (String) -> Unit) = block(kafka.bootstrapServers)

    override fun getProducerClass() = SpringKafkaProducer::class.java
    override fun getConsumerClass() = SpringKafkaConsumer::class.java
    override fun produceMessage(
        template: KafkaTemplate<String, String>,
        topic: String,
        message: String
    ) {
        SpringKafkaProducer(template).send(topic, message)
    }

    override fun getListener(body: ((ConsumerRecord<String, String>) -> Unit)): MessageListener<String, String> {
        return SpringKafkaConsumer(body)
    }
}
