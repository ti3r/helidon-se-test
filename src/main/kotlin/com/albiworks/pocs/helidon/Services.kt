package com.albiworks.pocs.helidon

import io.helidon.common.reactive.Multi
import io.helidon.config.Config
import io.helidon.messaging.Messaging
import io.helidon.messaging.connectors.kafka.KafkaConnector
import io.helidon.webserver.Routing
import io.helidon.webserver.ServerRequest
import io.helidon.webserver.ServerResponse
import io.helidon.webserver.Service
import org.apache.kafka.common.serialization.StringSerializer
import org.eclipse.microprofile.reactive.messaging.Message
import java.lang.RuntimeException

class KafkaMessageService: Service {

    private val config = Config.create()
    private val servers by lazy {config.get("kafka.servers").asString().orElseThrow { RuntimeException("kafka.servers not defined in properties") }}
    private val auth by lazy { config.get("kafka.auth").asString().orElseThrow { RuntimeException("kafka.auth not defined in properties") } }
    private val topic by lazy { config.get("kafka.topic").asString().orElseThrow { RuntimeException("kafka.topic not defined in properties") } }

    private val kafkaChannel = io.helidon.messaging.Channel.builder<String>()
        .name("producer")
        .subscriberConfig(KafkaConnector.configBuilder()
            .bootstrapServers(servers)
            .property("security.protocol", "SASL_SSL")
            .property("sasl.mechanism", "SCRAM-SHA-256")
            .property("sasl.jaas.config", auth)
            .topic(topic)
            .keySerializer(StringSerializer::class.java)
            .valueSerializer(StringSerializer::class.java)
            .build()
        ).build()

    private val kafkaConnector = KafkaConnector.create()

    override fun update(routingRules: Routing.Rules?) {
        routingRules?.get("/chat", this::getChat)
    }

    fun getChat(req: ServerRequest, resp: ServerResponse) {
        val msg = req.queryParams().first("msg").orElse("")

        val pub = Messaging.builder().publisher(kafkaChannel, Multi.just(msg).map { Message.of(it) })
            .connector(kafkaConnector)
            .build()
            .start()

        resp.status(200).send("Message sent correctly!!!")
    }
}