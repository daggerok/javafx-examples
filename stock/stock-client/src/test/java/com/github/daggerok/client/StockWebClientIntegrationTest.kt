package com.github.daggerok.client

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@SpringBootApplication
internal class StockWebClientIntegrationTestApplication

@SpringBootTest(classes = [StockWebClientIntegrationTestApplication::class])
class StockWebClientIntegrationTest(@Autowired private val stockWebClient: StockWebClient) {

  @Test
  fun test() {
    // // given
    // val webClient = WebClient.builder().build()
    // val stockWebClient = StockWebClient(webClient)
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
