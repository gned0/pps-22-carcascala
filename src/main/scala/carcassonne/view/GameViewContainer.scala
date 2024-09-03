package carcassonne.view

import scalafx.scene.layout.{HBox, Priority}

class GameViewContainer extends HBox {
  HBox.setHgrow(this, Priority.Always)
}
