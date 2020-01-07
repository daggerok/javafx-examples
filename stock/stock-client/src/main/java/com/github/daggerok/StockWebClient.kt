package com.github.daggerok

import org.apache.logging.log4j.LogManager
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration.ofSeconds
import java.time.LocalDateTime

class StockWebClient(private val webClient: WebClient) {
  companion object { private val log = LogManager.getLogger() }

  fun pricesFor(symbol: String) =
      webClient.get()
          .uri("http://127.0.0.1:8080/stocks/{symbol}", symbol)
          .retrieve()
          .bodyToFlux(StockPrice::class.java)
          .retryBackoff(3, ofSeconds(1), ofSeconds(5))
          .doOnNext(log::info)
          .doOnError(log::warn)
}

data class StockPrice(val symbol: String,
                      val price: Double,
                      val time: LocalDateTime)
