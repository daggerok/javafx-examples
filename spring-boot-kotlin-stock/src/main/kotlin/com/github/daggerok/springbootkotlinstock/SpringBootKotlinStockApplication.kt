package com.github.daggerok.springbootkotlinstock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKotlinStockApplication

fun main(args: Array<String>) {
  runApplication<SpringBootKotlinStockApplication>(*args)
}
