package carcassonne.model

import carcassonne.observers.SubjectGameMatch
import carcassonne.model.TileSegment.{N, S, W, E, C}

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
//  private def calculateCityPoints(meepleSegment: TileSegment, gameTile: GameTile): Int =
//    if isCityFeatureComplete(meepleSegment, gameTile) then
//      citySegments.size * 2
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

  def recursiveCityPointsCalculation(meepleSegment: TileSegment, position: Position): Int =
    val lastTilePosition = position
    var checkTilePosition = Position(0, 0)
    var cityOrientation = N
    meepleSegment match
      case N =>
        checkTilePosition = Position(lastTilePosition.x, lastTilePosition.y - 1)
        cityOrientation = S
      case S =>
        checkTilePosition = Position(lastTilePosition.x, lastTilePosition.y + 1)
        cityOrientation = N
      case W =>
        checkTilePosition = Position(lastTilePosition.x - 1, lastTilePosition.y)
        cityOrientation = E
      case E =>
        checkTilePosition = Position(lastTilePosition.x + 1, lastTilePosition.y)
        cityOrientation = W

    if map.getTileMap.get.contains(checkTilePosition) then
      if map.getTileMap.get(checkTilePosition).segments(cityOrientation) == SegmentType.City
        && map.getTileMap.get(checkTilePosition).segments(C) != SegmentType.City then
        return 1
      else
        val numPoints = map.getTile(checkTilePosition).
          get.segments.
          filter(segment => segment._2 == SegmentType.City &&
            List(N, S, W, E).contains(segment._1) &&
            segment._1 != cityOrientation).
          map(element => recursiveCityPointsCalculation(element._1, checkTilePosition)).sum
        return numPoints + 1

    -1