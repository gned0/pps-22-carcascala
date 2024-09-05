package carcassonne.observers.observers

import carcassonne.model.game.Player
import carcassonne.model.tile.{GameTile, GameTileFactory}
import scalafx.scene.image.{Image, ImageView}

trait ObserverGameMenuView:
  
  private var _drawnTile: (GameTile, ImageView) = (GameTileFactory.createStartTile(),
    ImageView(
      new Image(getClass.getResource("../../tiles/" + GameTileFactory.createStartTile().imagePath).toExternalForm)
    )
  )

  private var currentPlayer: Player = _

  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = player

  def getCurrentPlayer: Player = currentPlayer
  
  def getDrawnTile: (GameTile, ImageView) = _drawnTile
  
  def setDrawnTile(drawnTile: (GameTile, ImageView)): Unit =
    _drawnTile = drawnTile
    
  
