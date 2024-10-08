package carcassonne.model.game

import carcassonne.model.tile.TileSegment.{C, E, N, S, W}
import carcassonne.model.*
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.scalaprolog.PrologProcessing
import carcassonne.model.tile.SegmentType.{City, Field, Monastery, Road}
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.observers.subjects.model.SubjectGameMatch
import carcassonne.util.{Logger, Position}
import carcassonne.model.game.ScoreCalculator.*
import carcassonne.model.scalaprolog.PrologProcessing.*

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{Try, Success, Failure}

object GameState:
  private val MinPlayers = 2
  private val MaxPlayers = 5

/** Represents the state of the game.
  *
  * @param players
  *   the list of players participating in the game
  * @param board
  *   the game board
  * @param deck
  *   the deck of tiles
  */
class GameState(players: List[Player], board: CarcassonneBoard = CarcassonneBoard(), deck: TileDeck = TileDeck()) extends SubjectGameMatch:

  require(players.length >= GameState.MinPlayers, s"At least ${GameState.MinPlayers} players are required to start the game.")
  require(players.length <= GameState.MaxPlayers, s"No more than ${GameState.MaxPlayers} players are allowed in the game.")

  private var currentPlayerIndex: Int = 0

  /** Gets the current player.
    *
    * @return
    *   the current player
    */
  private def currentPlayer: Player = players(currentPlayerIndex)

  /** Draws a tile from the deck.
    */
  def drawTile(): Unit =
    deck.draw() match
      case Some(tile) =>
        notifyTileDrawn(tile, deck.getTileCount)
      case None =>
        calculateScore(true)
        notifyGameEnded(players)

  /** Places a tile on the board.
    *
    * @param gameTile
    *   the tile to place
    * @param position
    *   the position to place the tile
    * @return
    *   true if the tile was placed successfully, false otherwise
    */
  def placeTile(gameTile: GameTile, position: Position): Boolean =
    val isTilePlaced = board.placeTile(gameTile, position)
    notifyIsTilePlaced(isTilePlaced, position)
    isTilePlaced

  /** Places a follower on a tile segment.
    *
    * @param position
    *   the position of the tile
    * @param segment
    *   the segment of the tile
    * @param player
    *   the player placing the follower
    * @return
    *   true if the follower was placed successfully, false otherwise
    */
  def placeFollower(position: Position, segment: TileSegment, player: Player): Boolean =
    if board.placeFollower(position, segment, player) then
      player.placeFollower()
      Logger.log(
        "GAMESTATE",
        s"Player: ${player.name} placed a follower on tile: ${board.getTile(position)} on segment: $segment"
      )
      true
    else false

  /** Sends available follower positions for a given tile and position.
    *
    * @param gameTile
    *   the tile to check
    * @param position
    *   the position of the tile
    */
  def sendAvailableFollowerPositions(gameTile: GameTile, position: Position): Unit =
    val segmentMap = currentPlayer.getFollowers match
      case 0 => List.empty[TileSegment]
      case _ =>
        gameTile.segments.collect {
          case (segment, _) if {
                val connectedFeature = board.getConnectedFeature(position, segment)
                connectedFeature.nonEmpty &&
                !connectedFeature.exists { case (pos, seg) => board.getTile(pos).get.getFollowerMap.contains(seg) }
              } =>
            segment
        }.toList
    notifyAvailableFollowerPositions(segmentMap, position)

  /** Calculates the score for the players.
    *
    * @param endGame
    *   whether the game has ended
    */
  def calculateScore(endGame: Boolean): Unit =
    val followerTiles = board.getTileMap.get
      .filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.foreach((position, tile) =>
      tile.getFollowerMap.foreach((segment, playerID) =>
        players
          .filter(_.playerId == playerID)
          .foreach { p =>
            tile.segments(segment) match
              case Road =>
                if checkRoadCompleted(board, board.getConnectedFeature(position, segment)) then
                  val score = calculateRoadPoints(segment, position, board, endGame)
                  Logger.log(s"GAMESTATE SCORE", s"Player ${p.name} scored -> Road: $score")
                  finalizeScoreCalculation(p, position, tile, score)
              case City =>
                if checkCityCompleted(board, board.getConnectedFeature(position, segment)) then
                  val score = calculateCityPoints(segment, position, board, endGame)
                  Logger.log(s"GAMESTATE SCORE", s"Player ${p.name} scored -> City: $score")
                  finalizeScoreCalculation(p, position, tile, score)
              case Monastery =>
                if checkMonasteryCompleted(board, board.getConnectedFeature(position, segment)) then
                  val score = calculateMonasteryPoints(segment, position, board, endGame)
                  Logger.log(s"GAMESTATE SCORE", s"Player ${p.name} scored -> Monastery: $score")
                  finalizeScoreCalculation(p, position, tile, score)
              case Field =>
                if endGame then
                  val score = calculateFieldPoints(segment, position, board)
                  if score != 0 then
                    Logger.log(s"GAMESTATE SCORE", s"Player ${p.name} scored -> Field: $score")
                    finalizeScoreCalculation(p, position, tile, score)
              case _ => throw new IllegalArgumentException("Invalid segment type")
          }
      )
    )
    notifyScoreboardUpdated(createScoreboard())

  /** Finalizes the score calculation for a player for a particular feature by giving the score to the player, adding back its follower, removing the follower from the
    * board and notifying a score has been calculated.
    *
    * @param player
    *   the player who obtained the score for the follower place on a feature
    * @param position
    *   the position of the tile where the follower was placed
    * @param tile
    *   the tile of the tile where the follower was placed
    * @param score
    *   the score calculated
    */
  private def finalizeScoreCalculation(player: Player, position: Position, tile: GameTile, score: Int): Unit =
    player.addScore(score)
    player.returnFollower()
    board.removeFollower(board.getTile(position).get)
    notifyScoreCalculated(position, tile)

  /** Advances to the next player.
    */
  def nextPlayer(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length
    notifyPlayerChanged(currentPlayer)

  /** Initializes the first player and places the start tile.
    */
  def initializeFirstPlayer(): Unit =
    notifyPlayerChanged(currentPlayer)
    notifyScoreboardUpdated(createScoreboard())
    placeTile(GameTile.createStartTile(), Position(500, 500))

  /** Gets the list of players.
    *
    * @return
    *   the list of players
    */
  def getPlayers: List[Player] = players

  /** Gets the game board.
    *
    * @return
    *   the game board
    */
  def getBoard: CarcassonneBoard = board

  /** Creates a scoreboard mapping players to their scores.
    *
    * @return
    *   a map of players to their scores
    */
  private def createScoreboard(): Map[Player, Int] =
    players.map(player => player -> player.getScore).toMap
