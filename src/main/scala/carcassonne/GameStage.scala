package carcassonne

import carcassonne.controller.{GameMainViewsController, GameMatchController}
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{Color, GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.view.{GameBoardView, GameMatchView, GameStarterView, GameViewContainer}
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, Region, StackPane}

class GameStage(gameViewContainer: GameViewContainer) extends JFXApp3.PrimaryStage {
  title = "CarcaScala"
  scene = new Scene(850, 600):
    stylesheets.add(getClass.getResource("../placeholderTile.css").toExternalForm)
    root = gameViewContainer

  val starterView = GameStarterView()
  val viewsController = GameMainViewsController(this)
  starterView.addObserver(viewsController)
  gameViewContainer.children = starterView


  def setMainView(view: Node): Unit =
    gameViewContainer.children = view

}
