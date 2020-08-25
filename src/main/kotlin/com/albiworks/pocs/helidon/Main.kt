package com.albiworks.pocs.helidon

import io.helidon.config.Config
import io.helidon.health.HealthSupport
import io.helidon.metrics.MetricsSupport
import io.helidon.webserver.Handler
import io.helidon.webserver.Routing
import io.helidon.webserver.WebServer
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import java.time.LocalDateTime

private val config = Config.create()
private val port: Int by lazy { config.get("server.port").asInt().orElse(8080) }


//Create metrics service handler with default metrics
val metrics = MetricsSupport.create()
// Create readiness probe with star time
val live =
    HealthCheck { HealthCheckResponse.named("Started").up().withData("time", LocalDateTime.now().toString()).build() }
//Create health service handler
val healthCheck = HealthSupport.builder().addReadiness(live).build()
//Default hello handler
val helloHandler = Handler { req, resp -> resp.send("Hello World!!!") }

fun main(args: Array<String>) {

    WebServer.builder().port(port)
        .routing(Routing.builder()
            .register(metrics).register(healthCheck).register(KafkaMessageService())
            .get("/hello", helloHandler)
        )
        .config(config)
        .build().start()
        .thenAccept { print("Web server is running") }
        .exceptionally {
            println("Server startup failed with exception $it")
            return@exceptionally null
        }
}
