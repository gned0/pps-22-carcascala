package carcassonne

import carcassonne.controller.GameMatchController
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{Color, GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.view.{GameMapView, StarterView}
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, Region, StackPane}

class GameStage extends JFXApp3.PrimaryStage {

  // Initial mouse position
  private var initialX = 0.0
  private var initialY = 0.0

  // Zoom level
  private var zoomFactor = 1.0
  private val zoomIncrement = 0.1 // Zoom step size
  private val minZoom = 0.5       // Minimum zoom level
  private val maxZoom = 4.0       // Maximum zoom level

  title = "CarcaScala"
  scene = new Scene(850, 600):
    stylesheets.add(getClass.getResource("../placeholderTile.css").toExternalForm)

  def initializeGame(): Unit =
    val containerPane = HBox()
    val gameMapView = StackPane()

    def switchToGameView(): Unit =
      val view = GameMapView(() => switchToStarterView())
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

    val starterView = StarterView(() => switchToGameView())


    containerPane.children = starterView

    gameMapView.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    gameMapView.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
    gameMapView.setMaxSize(Double.MaxValue, Double.MaxValue)

    gameMapView.onMousePressed = (event: MouseEvent) =>
      initialX = event.sceneX
      initialY = event.sceneY

    gameMapView.onMouseDragged = (event: MouseEvent) =>
      val offsetX = event.sceneX - initialX
      val offsetY = event.sceneY - initialY

      gameMapView.translateX = gameMapView.translateX.value + offsetX
      gameMapView.translateY = gameMapView.translateY.value + offsetY

      initialX = event.sceneX
      initialY = event.sceneY

    gameMapView.onScroll = (event: ScrollEvent) =>
      zoomFactor = if event.deltaY > 0 then
        Math.min(zoomFactor + zoomIncrement, maxZoom) // Zoom in
      else
        Math.max(zoomFactor - zoomIncrement, minZoom) // Zoom out

      gameMapView.scaleX = zoomFactor
      gameMapView.scaleY = zoomFactor

      event.consume()

    this.getScene.setRoot(containerPane)

}
