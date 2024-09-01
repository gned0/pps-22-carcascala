package carcassonne

import carcassonne.controller.GameMatchController
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{Color, GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.view.{GameBoardView, GameMatchView, StarterView}
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, Region, StackPane}

class GameStage extends JFXApp3.PrimaryStage {

  title = "CarcaScala"
  scene = new Scene(850, 600):
    stylesheets.add(getClass.getResource("../placeholderTile.css").toExternalForm)

  def initializeGame(): Unit =
    val containerPane = HBox()
    val gameMapView = GameBoardView()

    def switchToGameView(): Unit =
      val view = GameMatchView(() => switchToStarterView())
      view.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
      view.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)

      val deck = TileDeck()
      val map = CarcassonneBoard()
      val game = GameMatch(List(Player(0, "test", Color.Red), Player(1, "test2", Color.Blue)), map, TileDeck())
      val controller = GameMatchController(game, view)
      controller.initialize()

      game.addObserver(view)


      gameMapView.children = view
      containerPane.children = gameMapView
      HBox.setHgrow(gameMapView, Always)
      game.play()
      view.addDrawnTilePane()

    def switchToStarterView(): Unit =
      val starterView = StarterView(() => switchToGameView())
      containerPane.children = starterView
    
    containerPane.children = StarterView(() => switchToGameView())

    this.getScene.setRoot(containerPane)

}
