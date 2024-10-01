package carcassonne

import carcassonne.controller.GameController
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.util.{Color, PlayerColor}
import carcassonne.view.gameMatch.{GameBoardView, GameMatchBoardView, GameMatchMenuView}
import carcassonne.view.GameViewContainer
import carcassonne.view.applicationStart.GameStarterView
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.geometry.Pos.{CenterRight, TopCenter}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.Priority.{Always, Never}
import scalafx.scene.layout.{GridPane, HBox, Priority, Region, StackPane, VBox}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Represents the primary stage of the Carcassonne game application.
 *
 * @param gameViewContainer The container for the game views.
 */
class GameStage(gameViewContainer: GameViewContainer) extends JFXApp3.PrimaryStage:
  title = "CarcaScala"
  scene = new Scene(1280, 720):
    root = gameViewContainer
    icons.add(new Image("CarcaScala-Logo-64x64.png"))

  gameViewContainer.children = new GameStarterView(playerNames => switchMainGameView(playerNames))

  /**
   * Switches the main game view to the game match view.
   *
   * @param playerNames The list of player names.
   */
  def switchMainGameView(playerNames: List[String]): Unit =
    val gameMenu = GameMatchMenuView(
      new GridPane:
        alignment = TopCenter
        padding = Insets(5, 0, 15, 0)
    )
    val centerButton = new Button:
      graphic = new ImageView(new Image("recenter.png")):
        preserveRatio = true
        fitHeight = 50
        fitWidth = 50
      prefWidth = 50
      prefHeight = 50
      margin = Insets(15)

    StackPane.setAlignment(gameMenu, CenterRight)
    StackPane.setAlignment(centerButton, Pos.TopLeft)

    val gameBoard = GameBoardView(centerButton)
    val boardView = GameMatchBoardView(() => gameEndedSwitchView())

    gameBoard.children.add(boardView)

    gameMenu.addObserver(boardView)

    val playersWithColors = PlayerColor.assignColors(playerNames)
    val players = playersWithColors.zipWithIndex.map{ case ((name, color), index) =>
      Player(index, name, color)
    }

    val game = GameState(players)
    game.addObserverBoard(boardView)
    game.addObserverMenu(gameMenu)
    GameController(game, boardView).initialize()

    this.setMainView(Seq(
      gameBoard, gameMenu, centerButton)
    )

  /**
   * Switches the view to the game starter view when the game ends.
   */
  def gameEndedSwitchView(): Unit =
    this.setMainView(Seq(new GameStarterView(playerNames => switchMainGameView(playerNames))))

  /**
   * Sets the main view of the game stage.
   *
   * @param views The sequence of views to be set as the main view.
   */
  private def setMainView(views: Seq[Node]): Unit =
    gameViewContainer.children = views