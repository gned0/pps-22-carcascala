package carcassonne.controller

import carcassonne.GameStage
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{Color, GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.observers.observers.ObserverStarterView
import carcassonne.view.{GameBoardView, GameMatchView, GameStarterView}
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Priority.Always

class GameMainViewsController(stage: GameStage) 
  extends ObserverStarterView[GameStarterView] {
  
  override def switchMainGameView(): Unit =
    val gameMapView = GameBoardView()
    val view = GameMatchView()

    val game = GameMatch(List(Player(0, "test", Color.Red), Player(1, "test2", Color.Blue)),
      CarcassonneBoard(),
      TileDeck())
    GameMatchController(game, view).initialize()
    game.addObserver(view)
    
    gameMapView.children = view
    stage.setMainView(view)

    HBox.setHgrow(gameMapView, Always)
    game.play()
    view.addDrawnTilePane()

  def switchToStarterView(): Unit =
    val starterView = GameStarterView()
    stage.setMainView(starterView)
}
