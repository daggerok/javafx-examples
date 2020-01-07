# Spring Boot Kotlin JavaFX
A simple Spring Boot Kotlin JavaFX application starter

1. generate spring-boot app using initializer
1. add JavaFX graphics dependency in `pom.xml` file:
   ```xml
   <dependencies>
       <dependency>
           <groupId>org.openjfx</groupId>
           <artifactId>javafx-graphics</artifactId>
           <version>${javafx.version}</version>
       </dependency>
       <dependency>
           <groupId>org.openjfx</groupId>
           <artifactId>javafx-fxml</artifactId>
           <version>${javafx.version}</version>
       </dependency>
   </dependencies>
   ```
1. implement minimals in `SpringBootKotlinJavaFxApplication.kt` file:
   ```kotlin
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
           .properties("spring.output.ansi.enabled=always")
           .properties("management.endpoint.shutdown.enabled=true")
           .properties("management.endpoints.web.exposure.include=health, info, shutdown")
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
   ```
