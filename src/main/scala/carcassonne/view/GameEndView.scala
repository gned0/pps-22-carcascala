package carcassonne.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.{Modality, Stage}
import scalafx.Includes.*
import scalafx.beans.property.*

object GameEndView {
  val popupStage: Stage = new Stage:
    initModality(Modality.ApplicationModal) // This makes the pop-up modal (blocking interaction with the main window)
    title = "Pop-Up Window"
    scene = new Scene(200, 100):
      root = new VBox:
        spacing = 10
        children = Seq(
          new Button("Close"):
            onAction = _ => popupStage.close()
        )
}
