package carcassonne.view

import carcassonne.observers.subjects.SubjectStarterView
import scalafx.application.Platform
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, TextField, Alert, ComboBox}
import scalafx.scene.layout.{GridPane, VBox, HBox}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

class GameStarterView(switchMainGameView: List[String] => Unit) extends VBox
  with SubjectStarterView[GameStarterView] {

  private val startGameButton = new Button("Start Game")
  private val exitGameButton = new Button("Exit Game")

  startGameButton.onMouseClicked = _ => showPlayerSetupDialog()
  exitGameButton.onMouseClicked = _ => Platform.exit()

  private val buttonBox = new HBox(10) // 10 pixels of spacing between buttons
  buttonBox.children.addAll(startGameButton, exitGameButton)
  buttonBox.alignment = Pos.Center // Center the buttons in the HBox

  this.children = buttonBox

  private def showPlayerSetupDialog(): Unit = {
    val playerSetupStage = new Stage {
      title = "Player Setup"
      scene = new Scene {
        root = createPlayerSetupPane()
      }
    }
    playerSetupStage.showAndWait()
  }

  private def createPlayerSetupPane(): VBox = {
    val playerNames = List.fill(5)(new TextField {
      promptText = "Enter player name"
      visible = false
    })

    val playerCountComboBox = new ComboBox[Int](2 to 5) {
      value = 2
      onAction = _ => {
        val selectedCount = value.value
        playerNames.zipWithIndex.foreach {
          case (textField, index) => textField.visible = index < selectedCount
        }
      }
    }

    playerNames.take(2).foreach(_.visible = true)

    val playerSetupPane = new VBox(10) {
      padding = Insets(20)
      alignment = Pos.Center

      children = Seq(
        new Label("Select number of players:"),
        playerCountComboBox
      ) ++ playerNames ++ Seq(
        new HBox(10) {
          alignment = Pos.Center
          children = Seq(
            new Button("Start Game") {
              onMouseClicked = _ => {
                val selectedNames = playerNames.filter(_.visible.value).map(_.text.value).filter(_.nonEmpty)
                if (selectedNames.size < 2) {
                  val alert = new Alert(AlertType.Warning) {
                    title = "Invalid Input"
                    headerText = "Not enough players"
                    contentText = "Please enter names for at least 2 players."
                  }
                  alert.showAndWait()
                } else {
                  switchMainGameView(selectedNames)
                  scene().getWindow.hide()
                }
              }
            },
            new Button("Cancel") {
              onMouseClicked = _ => scene().getWindow.hide()
            }
          )
        }
      )
    }
    playerSetupPane
  }
}
