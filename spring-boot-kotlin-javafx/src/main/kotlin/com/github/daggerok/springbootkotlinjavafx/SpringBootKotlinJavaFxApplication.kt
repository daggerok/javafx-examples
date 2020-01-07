package com.github.daggerok.springbootkotlinjavafx

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

data class StageReadyEvent(val stage: Any) : ApplicationEvent(stage)

@Component
class StageInitializer {

  @EventListener
  fun on(event: StageReadyEvent) {
    val stage = event.source
    if (stage is Stage) {
      println("initialization...")
    }
  }
}

@SpringBootApplication
class SpringBootKotlinJavaFxApplication : Application() {

  private lateinit var context: ConfigurableApplicationContext

  override fun init() {
    context = SpringApplicationBuilder()
        .web(WebApplicationType.REACTIVE)
        .sources(SpringBootKotlinJavaFxApplication::class.java)
        .registerShutdownHook(true)
        .run()
  }

  override fun start(stage: Stage) =
      context.publishEvent(StageReadyEvent(stage))

  @PreDestroy
  override fun stop() = Platform.exit().also {
    if (::context.isInitialized) context.close()
  }
}

fun main(args: Array<String>) {
  Application.launch(SpringBootKotlinJavaFxApplication::class.java, *args)
}
