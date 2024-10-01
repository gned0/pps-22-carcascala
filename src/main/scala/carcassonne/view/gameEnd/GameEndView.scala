package carcassonne.view.gameEnd

import carcassonne.model.game.Player
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.beans.property.*
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.{Modality, Stage}

class GameEndView(players: List[Player]):
  val popupStage: Stage = new Stage:
    initModality(Modality.ApplicationModal) // This makes the pop-up modal (blocking interaction with the main window)
    title = "Game Over"
    scene = new Scene(300, 100):
      root = new VBox:
        spacing = 10
        alignment = Pos.TopCenter
        children = createPlayerScoreLabels(players) :+
          new Button("Close"):
            alignment = Pos.BottomCenter
            onAction = _ => popupStage.close()

  // Function to create a label for each player showing their score
  private def createPlayerScoreLabels(players: List[Player]): Seq[Label] =
    players.map(player => new Label(s"${player.name}: ${player.getScore}") // Assuming Player has `name` and `score` fields
    )
