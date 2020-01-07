package com.github.daggerok.client

import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration.ofSeconds
import java.time.LocalDateTime

data class StockPrice(val symbol: String,
                      val price: Double,
                      val time: LocalDateTime)

class StockWebClient(private val webClient: WebClient) {
  companion object { private val log = LogManager.getLogger() }

  fun pricesFor(symbol: String) =
      webClient.get()
          .uri("/stocks/{symbol}", symbol)
          .retrieve()
          .bodyToFlux(StockPrice::class.java)
          .retryBackoff(3, ofSeconds(1), ofSeconds(5))
          .doOnNext(log::info)
          .doOnError(log::warn)
}

class StockRSocketClient(private val rSocketRequester: Mono<RSocketRequester>) {
  companion object { private val log = LogManager.getLogger() }

  fun pricesFor(symbol: String) = rSocketRequester.flatMapMany { rr ->
    rr.route("stocks")
        .data(symbol)
        .retrieveFlux(StockPrice::class.java)
        .retryBackoff(3, ofSeconds(1), ofSeconds(5))
        .doOnNext(log::info)
        .doOnError(log::warn)
  }
}

@Configuration
class StockClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun stockWebClient(webClient: WebClient) = StockWebClient(webClient)

  @Bean
  @ConditionalOnMissingBean
  fun webClient(builder: WebClient.Builder) = builder.baseUrl("http://127.0.0.1:8000").build()

  @Bean
  @ConditionalOnMissingBean
  fun stockRSocketClient(rr: Mono<RSocketRequester>) = StockRSocketClient(rr)

  @Bean
  @ConditionalOnMissingBean
  fun rSocketRequester(builder: RSocketRequester.Builder) = builder.connectTcp("127.0.0.1", 7000)
}
