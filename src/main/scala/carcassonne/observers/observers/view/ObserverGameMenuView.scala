package carcassonne.observers.observers.view

import carcassonne.model.game.Player
import carcassonne.model.tile.GameTile
import carcassonne.util.Position
import scalafx.scene.image.{Image, ImageView}

trait ObserverGameMenuView:
  
  private var _drawnTile: (GameTile, ImageView) = (GameTile.createStartTile(),
    new ImageView(
      new Image("tiles/" + GameTile.createStartTile().imagePath)
    ):
      fitWidth = 100
      fitHeight = 100
      preserveRatio = true
  )

  private var currentPlayer: Player = _

  def setCurrentPlayer(player: Player): Unit =
    currentPlayer = player

  def getCurrentPlayer: Player = currentPlayer
  
  def getDrawnTile: (GameTile, ImageView) = _drawnTile
  
  def setDrawnTile(drawnTile: (GameTile, ImageView)): Unit =
    _drawnTile = drawnTile
  
  def skipFollowerPlacement(position: Option[Position]): Unit
