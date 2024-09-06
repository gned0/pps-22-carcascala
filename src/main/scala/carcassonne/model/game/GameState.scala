package carcassonne.model.game

import carcassonne.model.tile.TileSegment.{C, E, N, S, W}
import carcassonne.model.*
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.observers.subjects.model.SubjectGameMatch
import carcassonne.util.Position

import scala.annotation.tailrec
import scala.collection.mutable

object GameState:
  private val MinPlayers = 2

class GameState(players: List[Player], board: CarcassonneBoard = CarcassonneBoard(), deck: TileDeck = TileDeck()) extends SubjectGameMatch:
  require(players.length >= GameState.MinPlayers, s"At least ${GameState.MinPlayers} players are required to start the game.")

  private var currentPlayerIndex: Int = 0

  private def currentPlayer: Player = players(currentPlayerIndex)

  def drawTile(): Unit =
    val tile = deck.draw()
    tile.foreach(t => notifyTileDrawn(t))

  def placeTile(gameTile: GameTile, position: Position): Boolean =
    val isTilePlaced = board.placeTile(gameTile, position)
    notifyIsTilePlaced(isTilePlaced, board.getTileMap, position)
    isTilePlaced

  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Boolean =
    if board.placeFollower(gameTile, segment, player) then
      player.placeFollower()
      notifyIsFollowerPlaced(gameTile, segment, player)
      true
    else
      false

  def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
    val segmentMap = gameTile.segments.collect {
      case (segment, _) if {
        val connectedFeature = board.getConnectedFeature(gameTile, segment)
        !connectedFeature.exists { case (tile, seg) => tile.followerMap.contains(seg) }
      } => segment
    }.toList
    println(segmentMap)
    notifyAvailableFollowerPositions(segmentMap, position)

  def isDeckEmpty: Boolean = deck.isEmpty

  def nextPlayer(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    notifyPlayerChanged(currentPlayer)

  def initializeFirstPlayer(): Unit =
    notifyPlayerChanged(currentPlayer)
  
  def getPlayers: List[Player] = players

  def getBoard: CarcassonneBoard = board






