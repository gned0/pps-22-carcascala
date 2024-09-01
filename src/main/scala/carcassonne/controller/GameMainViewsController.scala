package carcassonne.controller

import carcassonne.GameStage
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{Color, GameMatch, Player}
import carcassonne.model.tile.TileDeck
import carcassonne.observers.observers.ObserverStarterView
import carcassonne.view.{GameMatchView, GameStarterView}

class GameMainViewsController(stage: GameStage,
                              gameMatchView: GameMatchView,
                              starterView: GameStarterView) extends ObserverStarterView[GameStarterView] {
  
  override def switchMainGameView(): Unit =
    val view = GameMatchView()

    val game = GameMatch(List(Player(0, "test", Color.Red), Player(1, "test2", Color.Blue)),
      CarcassonneBoard(),
      TileDeck())
      GameMatchController(game, view).initialize()
    game.addObserver(view)


    gameMapView.children = view
    stage.setMainView(Seq(view))
    stage.getScene.getRoot.getChildren.add(gameMapView)
    
    HBox.setHgrow(gameMapView, Always)
    game.play()
    view.addDrawnTilePane()


}
