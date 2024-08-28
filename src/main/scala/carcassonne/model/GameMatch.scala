package carcassonne.model

import carcassonne.observers.SubjectGameMatch
import carcassonne.model.TileSegment.{C, E, N, S, W}

import scala.annotation.tailrec

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

//  def calculatePoints(gameTile: GameTile, adjacentTiles: Map[TileSegment, GameTile]): Int =
//    calculateCityPoints(gameTile, adjacentTiles) +
//      calculateRoadPoints(gameTile, adjacentTiles) +
//      calculateFieldPoints(gameTile, adjacentTiles)

//  private def calculateRoadPoints(gameTile: GameTile, adjacentTiles: Map[TileSegment, GameTile]): Int =
//    val roadSegments = gameTile.segments.filter(_._2 == SegmentType.Road).keys.toList
//
//    if isFeatureComplete(roadSegments, adjacentTiles, SegmentType.Road) then
//      roadSegments.size * 1
//    else 0
//
//  private def calculateFieldPoints(gameTile: GameTile, adjacentTiles: Map[TileSegment, GameTile]): Int =
//    val fieldSegments = gameTile.segments.filter(_._2 == SegmentType.Field).keys.toList
//
//    fieldSegments.count { pos =>
//      adjacentTiles.get(pos).exists(_.segments(pos) == SegmentType.City)
//    } * 3

  def calculateCityPoints(meepleSegment: TileSegment, position: Position): Int =
    (recursiveCityPointsCalculation(meepleSegment, position) + 1) * 2

  private def recursiveCityPointsCalculation(meepleSegment: TileSegment, position: Position): Int =

    @tailrec
    def helper(segmentsToCheck: List[(TileSegment, Position)], acc: Int): Int =
      segmentsToCheck match
        case Nil => acc
        case (segment, pos) :: tail =>
          val checkTilePosition = segment match
            case TileSegment.N => Position(pos.x, pos.y - 1)
            case TileSegment.S => Position(pos.x, pos.y + 1)
            case TileSegment.W => Position(pos.x - 1, pos.y)
            case TileSegment.E => Position(pos.x + 1, pos.y)
            case _ => pos
  
          val cityOrientation = segment match
            case TileSegment.N => TileSegment.S
            case TileSegment.S => TileSegment.N
            case TileSegment.W => TileSegment.E
            case TileSegment.E => TileSegment.W
            case _ => segment

          map.getTileMap.get.get(checkTilePosition) match
            case Some(tile) if tile.segments(cityOrientation) == SegmentType.City && tile.segments(TileSegment.C) != SegmentType.City =>
              helper(tail, acc + 1)
            case Some(tile) =>
              val newSegments = tile.segments.collect {
                case (seg, SegmentType.City) if List(TileSegment.N, TileSegment.S, TileSegment.W, TileSegment.E).contains(seg) && seg != cityOrientation =>
                  (seg, checkTilePosition)
              }.toList
              helper(newSegments ++ tail, acc + 1)
            case None =>
              helper(tail, acc)
            
    helper(List((meepleSegment, position)), 0)