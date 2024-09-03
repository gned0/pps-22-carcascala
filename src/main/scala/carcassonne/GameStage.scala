package carcassonne

import carcassonne.controller.GameMatchController
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.util.{Color, PlayerColor}
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

  // Pass a function that accepts a List[String]
  gameViewContainer.children = new GameStarterView(playerNames => switchMainGameView(playerNames))

  // Update this method to accept player names and assign colors
  def switchMainGameView(playerNames: List[String]): Unit = {
    val gameBoard = GameBoardView()
    val boardView = GameMatchView(() => gameEndedSwitchView())
    gameBoard.children = boardView

    val playersWithColors = PlayerColor.assignColors(playerNames)
    val players = playersWithColors.zipWithIndex.map {
      case ((name, color), index) => Player(index, name, color)
    }

    val game = GameMatch(players, CarcassonneBoard(), TileDeck())
    GameMatchController(game, boardView).initialize()
    game.addObserver(boardView)

    this.setMainView(boardView)

    game.play()
    boardView.addDrawnTilePane()
  }

  def gameEndedSwitchView(): Unit =
    this.setMainView(new GameStarterView(playerNames => switchMainGameView(playerNames)))

  private def setMainView(view: Node): Unit =
    gameViewContainer.children = view
}
