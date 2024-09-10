package carcassonne.model.game

import carcassonne.model.tile.TileSegment.{C, E, N, S, W}
import carcassonne.model.*
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.SegmentType.{City, Road}
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.observers.subjects.model.SubjectGameMatch
import carcassonne.util.{Logger, Position}

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
      Logger.log(s"GAMESTATE", s"Player: " + player.name + " placed a follower on tile: " + gameTile +
        " on segment: " + segment)
      true
    else
      false

  def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
    val segmentMap = gameTile.segments.collect {
      case (segment, _) if {
        val connectedFeature = board.getConnectedFeature(gameTile, segment)
        connectedFeature.nonEmpty && 
          !connectedFeature.exists { case (tile, seg) => tile.followerMap.contains(seg) }
      } => segment
    }.toList
    notifyAvailableFollowerPositions(segmentMap, position)

  def calculateScore(): Unit =
    val followerTiles = board.getTileMap.get
        .filter((_, tile) => tile.followerMap.nonEmpty )
    followerTiles.foreach((position, tile) =>
      tile.followerMap.foreach((segment, playerID) =>
        players.filter(p => p.playerId == playerID)
        .map(p =>
          if tile.segments(segment) == Road then
            val score = ScoreCalculator().calculateRoadPoints(segment, position, board)
            if score != 0 then
              println("Road: " + score)
//              p.addScore(score)
//              notifyScoreCalculated(position, tile)

          else if tile.segments(segment) == City then
            val score = ScoreCalculator().calculateCityPoints(segment, position, board, false)
            if score != 0 then
              println("City: " + score)
//              p.addScore(score)
//              notifyScoreCalculated(position, tile)
              
        )
      )
    )


  def isDeckEmpty: Boolean = deck.isEmpty

  def nextPlayer(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    notifyPlayerChanged(currentPlayer)

  def initializeFirstPlayer(): Unit =
    notifyPlayerChanged(currentPlayer)
    placeTile(GameTile.createStartTile(), Position(500, 500))
  
  def getPlayers: List[Player] = players

  def getBoard: CarcassonneBoard = board






