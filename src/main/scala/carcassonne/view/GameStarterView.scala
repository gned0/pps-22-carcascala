package carcassonne.view

import carcassonne.observers.subjects.SubjectStarterView
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{HBox, VBox}

class GameStarterView(switchMainGameView: () => Unit) extends VBox
  with SubjectStarterView[GameStarterView]{
  val startGameButton = new Button("Start Game")
  startGameButton.onMouseClicked = _ => switchMainGameView()

  val exitGameButton = new Button("Exit Game")
  exitGameButton.onMouseClicked = _ => Platform.exit()


  val buttonBox = new HBox(10) // 10 pixels of spacing between buttons
  buttonBox.children.addAll(startGameButton, exitGameButton)
  buttonBox.alignment = Pos.Center // Center the buttons in the HBox

  this.children = buttonBox
}
