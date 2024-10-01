package carcassonne.model.game

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.{SegmentType, TileSegment}
import carcassonne.util.Position
import carcassonne.util.adjacency.ScoreAdjacencyPositions

import scala.annotation.nowarn

/** Class responsible for calculating scores in the Carcassonne game.
  */
object ScoreCalculator:

  /** Calculates the points for a city feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the city
    */
  def calculateCityPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val points = cityPointsCalculation(followerSegment, position, map, endGame)
    if endGame then points else points * 2

  /** Helper method to calculate the points for a city feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the city
    */
  private def cityPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    if isCityFinished(connectedFeatures, map) || endGame then connectedFeatures.groupBy(_._1).map(_._2.head).toSet.size
    else 0

  /** Checks if a city feature is finished.
    *
    * @param connectedFeatures
    *   the set of connected features
    * @param map
    *   the current state of the game board
    * @return
    *   true if the city is finished, false otherwise
    */
  private def isCityFinished(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Boolean =
    connectedFeatures.forall { (pos, segment) =>
      ScoreAdjacencyPositions().getAdjacentCityTilesAndSegments(pos, segment).forall {
        case (Some(connectedPos), adjSegment) =>
          map.getTile(connectedPos).exists(_.segments(adjSegment) == SegmentType.City)
        case _ => false
      }
    }

  /** Calculates the points for a road feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the road
    */
  def calculateRoadPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    roadPointsCalculation(followerSegment, position, map, endGame)

  /** Helper method to calculate the points for a road feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the road
    */
  private def roadPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    if isRoadFinished(connectedFeatures, map) || endGame then connectedFeatures.groupBy(_._1).map(_._2.head).toSet.size
    else 0

  /** Checks if a road feature is finished.
    *
    * @param connectedFeatures
    *   the set of connected features
    * @param map
    *   the current state of the game board
    * @return
    *   true if the road is finished, false otherwise
    */
  private def isRoadFinished(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Boolean =
    connectedFeatures.forall { (tile, segment) =>
      ScoreAdjacencyPositions().getAdjacentRoadTilesAndSegments(tile, segment).forall {
        case (Some(connectedPos), adjSegment) =>
          map.getTile(connectedPos).exists(_.segments(adjSegment) == SegmentType.Road)
        case _ => false
      }
    }

  /** Calculates the points for a monastery feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the monastery
    */
  def calculateMonasteryPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    monasteryPointsCalculation(followerSegment, position, map, endGame)

  /** Helper method to calculate the points for a monastery feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @param endGame
    *   whether the calculation is for the end of the game
    * @return
    *   the calculated points for the monastery
    */
  private def monasteryPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    val monasterySurroundings = calculateMonasterySurroundings(connectedFeatures, map)
    if monasterySurroundings == 9 || endGame then monasterySurroundings else 0

  /** Calculates the number of surrounding tiles for a monastery.
    *
    * @param connectedFeatures
    *   the set of connected features
    * @param map
    *   the current state of the game board
    * @return
    *   the number of surrounding tiles
    */
  private def calculateMonasterySurroundings(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Int =
    val monasteryPosition = connectedFeatures.head._1
    (for
      i <- -1 to 1
      j <- -1 to 1
      pos = Position(monasteryPosition.x + i, monasteryPosition.y + j)
      if map.getTile(pos).isDefined
    yield pos).size

  /** Calculates the points for a field feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @return
    *   the calculated points for the field
    */
  def calculateFieldPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard): Int =
    fieldPointsCalculation(followerSegment, position, map)

  /** Helper method to calculate the points for a field feature.
    *
    * @param followerSegment
    *   the segment where the follower is placed
    * @param position
    *   the position of the tile
    * @param map
    *   the current state of the game board
    * @return
    *   the calculated points for the field
    */
  private def fieldPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    getCitiesNextToFields(connectedFeatures, map) * 3

  /** Gets the number of cities adjacent to field features.
    *
    * @param connectedFeatures
    *   the set of connected features
    * @param map
    *   the current state of the game board
    * @return
    *   the number of adjacent cities
    */
  private def getCitiesNextToFields(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Int =
    var result = 0
    var visited: Set[(Position, TileSegment)] = Set.empty
    for (tile, segment) <- connectedFeatures do
      for (adjPos, adjSegment) <- ScoreAdjacencyPositions().getAdjacentFieldSegments(tile, segment) do
        adjPos match
          case Some(connectedPos) if map.getTile(connectedPos).nonEmpty =>
            map.getTile(connectedPos).get.segments(adjSegment) match
              case SegmentType.City =>
                val connectedCityFeatures = map.getConnectedFeature(connectedPos, adjSegment)
                if connectedCityFeatures.intersect(visited).isEmpty then
                  visited ++= connectedCityFeatures
                  if isCityFinished(connectedCityFeatures, map) then result += 1
              case _ => ()

    result
