package com.github.daggerok.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class StockAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun stockWebClient(webClient: WebClient) = StockWebClient(webClient)

  @Bean
  @ConditionalOnMissingBean
  fun webClient() = WebClient.builder().build()
}
