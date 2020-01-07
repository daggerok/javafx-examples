package com.github.daggerok.client

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootApplication
internal class StockRSocketClientIntegrationTestApplication // this one is required only by this test class!

@SpringBootTest(classes = [StockRSocketClientIntegrationTestApplication::class])
class StockRSocketClientIntegrationTest(@Autowired private val rSocketClient: StockRSocketClient) {

  @Test
  fun test() {
    // when
    StepVerifier.create(rSocketClient.pricesFor("SYMBOL")
        .take(5))
    // then
        .expectNextMatches { item: StockPrice? ->
          item?.symbol == "SYMBOL"
        }
        .expectNextCount(4)
        .verifyComplete()
  }
}
