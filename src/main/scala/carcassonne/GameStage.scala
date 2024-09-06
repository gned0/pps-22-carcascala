package carcassonne

import carcassonne.controller.GameController
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.util.{Color, PlayerColor}
import carcassonne.view.{GameBoardView, GameMatchBoardView, GameMatchMenuView, GameStarterView, GameViewContainer}
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.Priority.{Always, Never}
import scalafx.scene.layout.{GridPane, HBox, Priority, Region, StackPane, VBox}

class GameStage(gameViewContainer: GameViewContainer) extends JFXApp3.PrimaryStage {
  title = "CarcaScala"
  scene = new Scene(850, 600):
    stylesheets.add(getClass.getResource("../placeholderTile.css").toExternalForm)
    root = gameViewContainer

  // Pass a function that accepts a List[String]
  gameViewContainer.children = new GameStarterView(playerNames => switchMainGameView(playerNames))

  // Update this method to accept player names and assign colors
  def switchMainGameView(playerNames: List[String]): Unit = {
    val gameMenu = GameMatchMenuView(
      new GridPane():
        alignment = TopCenter
        vgrow = Always
    )

    val gameBoard = GameBoardView()
    val boardView = GameMatchBoardView(() => gameEndedSwitchView())
    gameBoard.children = boardView
    
    gameMenu.addObserver(boardView)

    // Set HGrow priorities
    HBox.setHgrow(gameBoard, Priority.Always)
    HBox.setHgrow(gameMenu, Priority.Never)

    val playersWithColors = PlayerColor.assignColors(playerNames)
    val players = playersWithColors.zipWithIndex.map {
      case ((name, color), index) => Player(index, name, color)
    }

    val game = GameState(players)
    game.addObserverBoard(boardView)
    game.addObserverMenu(gameMenu)
    GameController(game, boardView).initialize()

    this.setMainView(Seq(gameBoard, gameMenu))
  }

  def gameEndedSwitchView(): Unit =
    this.setMainView(Seq(new GameStarterView(playerNames => switchMainGameView(playerNames))))

  private def setMainView(views: Seq[Node]): Unit =
    gameViewContainer.children = views
}