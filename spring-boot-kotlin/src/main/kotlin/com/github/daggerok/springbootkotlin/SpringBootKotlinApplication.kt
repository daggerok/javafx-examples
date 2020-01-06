package com.github.daggerok.springbootkotlin

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

data class StageReadyEvent(val stage: Any) : ApplicationEvent(stage)

class JavaFxApplication : Application() {
  companion object { private val log = LogManager.getLogger() }

  private lateinit var context: ConfigurableApplicationContext

  override fun init() {
    context = SpringApplicationBuilder()
        .sources(SpringBootKotlinApplication::class.java)
        .registerShutdownHook(true)
        .run()
    log.info("Initialized spring boot context {}", context)
  }

  override fun start(primaryStage: Stage?) {
    val stage = primaryStage ?: throw IllegalArgumentException("Stage is required.")
    log.info("primary stage created {}", primaryStage)

    val event = StageReadyEvent(stage)
    log.info("publishing {}", event)
    context.publishEvent(event)
  }

  override fun stop() {
    log.info("Bye...")
    context.close()
    Platform.exit()
  }
}

@Component
class StageInitializer {
  companion object { private val log = LogManager.getLogger() }

  @EventListener
  fun on(event: StageReadyEvent) {
    val stage = event.source
    if (stage is Stage) {
      log.info("received {}", event)
    }
  }
}

@SpringBootApplication
class SpringBootKotlinApplication

fun main(args: Array<String>) {
  Application.launch(JavaFxApplication::class.java, *args)
}
