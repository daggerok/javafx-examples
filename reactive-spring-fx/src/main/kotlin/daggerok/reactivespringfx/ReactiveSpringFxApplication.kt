package daggerok.reactivespringfx

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.springframework.boot.WebApplicationType.REACTIVE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

fun main(args: Array<String>) {
    Application.launch(ReactiveSpringFxApplication::class.java, *args)
}

data class StageReadyEvent(val stage: Stage) : ApplicationEvent(stage)

@SpringBootApplication
class ReactiveSpringFxApplication : Application() {

    lateinit var applicationContext: ConfigurableApplicationContext

    override fun init() {
        applicationContext = SpringApplicationBuilder(ReactiveSpringFxApplication::class.java)
            .web(REACTIVE)
            .sources(ReactiveSpringFxApplication::class.java)
            .properties("spring.output.ansi.enabled=always")
            .registerShutdownHook(true)
            .run()
    }

    override fun start(stage: Stage) =
        applicationContext.publishEvent(StageReadyEvent(stage))

    override fun stop() =
        applicationContext.close()
}

@Component
class MainInitializer {

    @EventListener
    fun onApplicationEvent(event: StageReadyEvent) {
        val stage = event.source
        println("initialization... $stage")
        if (stage !is Stage) return

        val gridPane = GridPane()
        gridPane.alignment = Pos.CENTER
        gridPane.hgap = 10.0
        gridPane.vgap = 10.0

        stage.title = "Hello, World!"
        stage.scene = Scene(gridPane, 600.0, 400.0)
        stage.show()

        // register shutdown app on window close:
        stage.setOnCloseRequest { Platform.exit() }
    }
}

// @Component
// class MainController
