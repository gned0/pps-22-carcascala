package carcassonne

import carcassonne.controller.GameMatchController
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
  
  gameViewContainer.children = GameStarterView(() => switchMainGameView())

  def switchMainGameView(): Unit =
    val gameBoard = GameBoardView()
    val boardView = GameMatchView(() => gameEndedSwitchView())

    val game = GameMatch(List(Player(0, "test", Color.Red), Player(1, "test2", Color.Blue)),
      CarcassonneBoard(),
      TileDeck())
    GameMatchController(game, boardView).initialize()
    game.addObserver(boardView)

    gameBoard.children = boardView
    this.setMainView(boardView)

    HBox.setHgrow(gameBoard, Always)
    game.play()
    boardView.addDrawnTilePane()

  def gameEndedSwitchView(): Unit =
    this.setMainView(GameStarterView(() => switchMainGameView()))

  private def setMainView(view: Node): Unit =
    gameViewContainer.children = view
}
