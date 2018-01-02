package daggerok

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class App : Application() {

  override fun start(primaryStage: Stage?) {

    val gridPane = GridPane()
    gridPane.alignment = Pos.CENTER
    gridPane.hgap = 10.0
    gridPane.vgap = 10.0

    primaryStage?.title = JavaClass.nevermind("Hello, World!")
    primaryStage?.scene = Scene(gridPane, 600.0, 400.0)
    primaryStage?.show()
  }

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(App::class.java, *args)
    }
  }
}
