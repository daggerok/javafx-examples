package com.github.daggerok.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import java.time.Duration.ofSeconds
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Service
class PriceService {

  fun pricesFor(symbol: String) =
      Flux.interval(ofSeconds(1))
          .map { StockPrice(symbol, genPrice(), now()) }
          .retryBackoff(5, ofSeconds(2), ofSeconds(20))

  fun genPrice() = ThreadLocalRandom.current().nextDouble(100.0)
}

@Controller
class RSocketController(private val priceService: PriceService) {

  @MessageMapping("stocks")
  fun pricesFor(symbol: String) = priceService.pricesFor(symbol)
}

@RestController
class RestController(private val priceService: PriceService) {

  @GetMapping(
      path = ["/stocks/{symbol}"],
      produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
  )
  fun pricesFor(@PathVariable symbol: String) = priceService.pricesFor(symbol)

  @RequestMapping("/{actuator:!actuator}/**", "/**") // @RequestMapping("/**")
  fun fallback(@PathVariable actuator: Optional<String>, exchange: ServerWebExchange) = mapOf(
      "api" to mapOf(
          "stock prices GET" to exchange.withBase("/stocks/{symbol}")
      )
  )

  fun ServerWebExchange.withBase(path: String) =
      "${this.request.uri.scheme}://${this.request.uri.authority}$path"
}

data class StockPrice(val symbol: String,
                      val price: Double,
                      val time: LocalDateTime)

@SpringBootApplication
class StockService

fun main(args: Array<String>) {
  runApplication<StockService>(*args)
}
