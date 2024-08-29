package carcassonne.model

import carcassonne.observers.SubjectGameMatch
import carcassonne.model.TileSegment.{C, E, N, S, W}

import scala.annotation.tailrec
import scala.collection.mutable

object GameMatch:
  private val MinPlayers = 2

class GameMatch(players: List[Player], map: CarcassonneBoard, deck: TileDeck) extends SubjectGameMatch[GameMatch]:
  require(players.length >= GameMatch.MinPlayers, s"At least ${GameMatch.MinPlayers} players are required to start the game.")

  private var currentPlayerIndex: Int = 0

  private def currentPlayer: Player = players(currentPlayerIndex)

  private def endTurn(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length

  private def isGameOver: Boolean = deck.isEmpty

  private def takeTurn(): Unit =
    if deck.isEmpty then
      gameEnded()
    else
      val tile = deck.draw()
      notifyTileDrawn(tile.get)
    // map.placeTile(tile.get, Position(userInput))
    // map.placeFollower(Position(userInput)
    // scoring.computeScore(map)

//    endTurn()

  def placeTile(gameTile: GameTile, position: Position): Unit =
    val isTilePlaced = map.placeTile(gameTile, position)
    notifyIsTilePlaced(isTilePlaced, map.getTileMap, position)

    endTurn()
    takeTurn()

  def play(): Unit =
    takeTurn()

  def gameEnded(): Unit =
    notifyGameEnded(players)
    println("Game over! Final scores:")
    players.foreach(p => println(s"${p.name}: ${p.score}"))



  def placeMeeple(gameTile: GameTile, segment: TileSegment, player: Player): Boolean =
    if !gameTile.followerMap.contains(segment) then
      gameTile.followerMap = gameTile.followerMap.updated(segment, Some(player).get.playerId)
      true
    else false


  