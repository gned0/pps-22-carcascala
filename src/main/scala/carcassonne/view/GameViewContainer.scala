package carcassonne.view

import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{HBox, Priority}

class GameViewContainer extends HBox {
  HBox.setHgrow(this, Priority.Always)
  this.alignment = Center
}
