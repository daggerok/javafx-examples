package com.github.daggerok.client

import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

class StockWebClientIntegrationTest {

  @Test
  fun test() {
    // given
    val webClient = WebClient.builder().build()
    val stockWebClient = StockWebClient(webClient)
    // when
    StepVerifier.create(stockWebClient.pricesFor("SYMBOL")
        .take(5))
    // then
        .expectNextMatches { item: StockPrice? ->
          item?.symbol == "SYMBOL"
        }
        .expectNextCount(4)
        .verifyComplete()
  }
}
