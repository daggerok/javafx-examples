package com.github.daggerok.ui

import com.github.daggerok.client.StockPrice
import com.github.daggerok.client.StockRSocketClient
import com.github.daggerok.client.StockWebClient
import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.FXCollections.*
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.util.function.Consumer
import javax.annotation.PreDestroy

data class StageReadyEvent(val stage: Any) : ApplicationEvent(stage)

class PriceSubscriber(symbol: String) : Consumer<StockPrice> {
  private val seriesData = observableArrayList<Data<String, Double>>()
  val series = Series(symbol, seriesData)

  override fun accept(price: StockPrice) = Platform.runLater {
    val data = Data(price.time.second.toString(), price.price)
    seriesData.add(data)
  }
}

@Component
class ChartController(private val webclient: StockWebClient,
                      private val rSocketClient: StockRSocketClient) {
  @FXML lateinit var chart: LineChart<String, Double>

  @FXML
  fun initialize() {
    chart.data = observableArrayList<Series<String, Double>>()

    val symbol = "SYMBOL"
    val priceSubscriber = PriceSubscriber(symbol)
    chart.data.add(priceSubscriber.series)
    webclient.pricesFor(symbol).subscribe(priceSubscriber)

    val symbol2 = "SYMBOL2"
    val priceSubscriber2 = PriceSubscriber(symbol2)
    chart.data.add(priceSubscriber2.series)
    rSocketClient.pricesFor(symbol).subscribe(priceSubscriber2)
  }
}

@Component
class StageInitializer(private val applicationContext: ApplicationContext,
                       @Value("\${classpath*:/chart.fxml}") private val chartResource: Resource,
                       @Value("\${spring.application.name}") private val applicationName: String) {

  @EventListener
  fun on(event: StageReadyEvent) {
    val stage = event.source
    if (stage is Stage) {
      println("initialization...")
      val fxmlLoader = FXMLLoader(chartResource.url)
      fxmlLoader.controllerFactory = Callback {
        applicationContext.getBean(it)
      }
      val parent = fxmlLoader.load<Parent>()
      stage.scene = Scene(parent, 800.0, 600.0)
      stage.title = applicationName
      stage.show()
    }
  }
}

@SpringBootApplication
class StockUI : Application() {

  private lateinit var context: ConfigurableApplicationContext

  override fun init() {
    context = SpringApplicationBuilder()
        .web(WebApplicationType.REACTIVE)
        .sources(StockUI::class.java)
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
  Application.launch(StockUI::class.java, *args)
}
