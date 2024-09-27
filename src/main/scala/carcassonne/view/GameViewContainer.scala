package carcassonne.view

import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox, Priority, StackPane}
import carcassonne.util.Color as SFXColor
import scalafx.geometry.Insets

class GameViewContainer extends StackPane {
  this.alignment = Center
  this.background = new Background(Array(new BackgroundFill(SFXColor.getCustomSFXColor(117, 177, 31, 1), CornerRadii.Empty, Insets.Empty)))
}
