package carcassonne.model.game

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.{Logger, Position}

import scala.annotation.tailrec
import scala.collection.mutable

class ScoreCalculator {

  /**
   * Calculates the points for a city based on the given meeple segment and position.
   *
   * @param followerSegment The segment of the tile where the meeple is placed.
   * @param position        The position of the tile on the board.
   * @param map             The current state of the Carcassonne board.
   * @return The total points for the city.
   */
  def calculateCityPoints(followerSegment: TileSegment,
                          position: Position,
                          map: CarcassonneBoard,
                          endGame: Boolean): Int =
    if endGame then
      cityPointsCalculation(followerSegment, position, map, endGame)
    else
      cityPointsCalculation(followerSegment, position, map, endGame) * 2

  private def cityPointsCalculation(meepleSegment: TileSegment,
                                    position: Position,
                                    map: CarcassonneBoard,
                                    endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    if isCityFinished(connectedFeatures, map) || endGame then
      val uniqueTiles: Set[(Position, TileSegment)] = connectedFeatures
        .groupBy(_._1)
        .map { case (gameTile, tuples) => tuples.head }
        .toSet
      uniqueTiles.size
    else
      0

  private def isCityFinished(connectedFeatures: Set[(Position, TileSegment)],
                             map: CarcassonneBoard): Boolean = {
    for ((pos, segment) <- connectedFeatures) {
      val neighbors = getAdjacentTilesAndSegments(pos, segment)
      for ((adjPos, adjSegment) <- neighbors) {
        adjPos match {
          case Some(connectedPos) if map.getTile(connectedPos).nonEmpty =>
            val isConnectedCity = map.getTile(connectedPos).get.segments(adjSegment) match {
              case SegmentType.City => true
              case _ => false
            }
            if (!isConnectedCity) {
              return false
            }
          case _ =>
            return false
        }
      }
    }
    true
  }

  /**
   * This helper method checks all the adjacent tiles and segments for a given tile and segment.
   * It returns a list of adjacent tiles and segments.
   */
  private def getAdjacentTilesAndSegments(position: Position,
                                          segment: TileSegment): List[(Option[Position], TileSegment)] = {
    segment match {
      case TileSegment.N => List((Some(Position(position.x, position.y - 1)), TileSegment.S))
      case TileSegment.S => List((Some(Position(position.x, position.y + 1)), TileSegment.N))
      case TileSegment.E => List((Some(Position(position.x + 1, position.y)), TileSegment.W))
      case TileSegment.W => List((Some(Position(position.x - 1, position.y)), TileSegment.E))
      case TileSegment.NE => List(
        (Some(Position(position.x, position.y - 1)), TileSegment.S),
        (Some(Position(position.x + 1, position.y)), TileSegment.W)
      )
      case TileSegment.NW => List(
        (Some(Position(position.x, position.y - 1)), TileSegment.S),
        (Some(Position(position.x - 1, position.y)), TileSegment.E)
      )
      case TileSegment.SE => List(
        (Some(Position(position.x, position.y + 1)), TileSegment.N),
        (Some(Position(position.x + 1, position.y)), TileSegment.W)
      )
      case TileSegment.SW => List(
        (Some(Position(position.x, position.y + 1)), TileSegment.N),
        (Some(Position(position.x - 1, position.y)), TileSegment.E)
      )
      case _ => List()
    }
  }


  def calculateRoadPoints(followerSegment: TileSegment,
                          position: Position,
                          map: CarcassonneBoard,
                          endGame: Boolean): Int =
    roadPointsCalculation(followerSegment, position, map, endGame)

  private def roadPointsCalculation(meepleSegment: TileSegment,
                                    position: Position,
                                    map: CarcassonneBoard,
                                    endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    if isRoadFinished(connectedFeatures, map) || endGame then
      val uniqueTiles: Set[(Position, TileSegment)] = connectedFeatures
        .groupBy(_._1)
        .map { case (gameTile, tuples) => tuples.head }
        .toSet
      uniqueTiles.size
    else
      0

  private def isRoadFinished(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Boolean = {
    for ((tile, segment) <- connectedFeatures) {
      val neighbors = getAdjacentRoadTilesAndSegments(tile, segment)
      for ((adjPos, adjSegment) <- neighbors) {
        adjPos match {
          case Some(connectedPos) if map.getTile(connectedPos).nonEmpty =>
            val isRoadConnected = map.getTile(connectedPos).get.segments(adjSegment) match {
              case SegmentType.Road => true
              case _ => false
            }
            if (!isRoadConnected) {
              return false
            }
          case _ =>
            return false
        }
      }
    }
    true
  }

  private def getAdjacentRoadTilesAndSegments(position: Position,
                                              segment: TileSegment): List[(Option[Position], TileSegment)] = {
    segment match {
      case TileSegment.N => List((Some(Position(position.x, position.y - 1)), TileSegment.S))
      case TileSegment.S => List((Some(Position(position.x, position.y + 1)), TileSegment.N))
      case TileSegment.E => List((Some(Position(position.x + 1, position.y)), TileSegment.W))
      case TileSegment.W => List((Some(Position(position.x - 1, position.y)), TileSegment.E))
      case _ => List()
    }
  }


  def calculateMonasteryPoints(followerSegment: TileSegment,
                               position: Position,
                               map: CarcassonneBoard,
                               endGame: Boolean): Int =
    monasteryPointsCalculation(followerSegment, position, map, endGame)

  private def monasteryPointsCalculation(meepleSegment: TileSegment,
                                         position: Position,
                                         map: CarcassonneBoard,
                                         endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    val monasterySurroundings = calculateMonasterySurroundings(connectedFeatures, map)
    if monasterySurroundings == 9 || endGame then
      monasterySurroundings
    else
      0

  private def calculateMonasterySurroundings(connectedFeatures: Set[(Position, TileSegment)],
                                             map: CarcassonneBoard): Int =
    val monasteryPosition = connectedFeatures.head._1
    var tilesPresent = 0
    for i <- -1 to 1 do
      for j <- -1 to 1 do
        val pos = Position(monasteryPosition.x + i, monasteryPosition.y + j)
        if map.getTile(pos).isEmpty then
          tilesPresent = tilesPresent + 1
    tilesPresent


  def calculateFieldPoints(followerSegment: TileSegment,
                           position: Position,
                           map: CarcassonneBoard): Int =
    fieldPointsCalculation(followerSegment, position, map)

  private def fieldPointsCalculation(meepleSegment: TileSegment,
                                     position: Position,
                                     map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    getCitiesNextToFields(connectedFeatures, map) * 3

  private def getCitiesNextToFields(connectedFeatures: Set[(Position, TileSegment)],
                                    map: CarcassonneBoard): Int =
    var result = 0
    var visited: Set[(Position, TileSegment)] = Set.empty
    for ((tile, segment) <- connectedFeatures) {
      val neighbors = getAdjacentFieldSegments(tile, segment)
      for ((adjPos, adjSegment) <- neighbors) {
        adjPos match {
          case Some(connectedPos) if map.getTile(connectedPos).nonEmpty =>
            map.getTile(connectedPos).get.segments(adjSegment) match {
              case SegmentType.City =>
                val connectedCityFeatures = map.getConnectedFeature(
                  connectedPos,
                  adjSegment
                )
                if connectedCityFeatures.intersect(visited).isEmpty then
                  visited = visited ++ connectedCityFeatures
                  if isCityFinished(connectedCityFeatures, map) then
                    result = result + 1
              case _ =>
            }
        }
      }
    }
    result

  private def getAdjacentFieldSegments(position: Position,
                                       segment: TileSegment): List[(Option[Position], TileSegment)] = {
    segment match {
      case TileSegment.N => List(
        (Some(position), TileSegment.NW),
        (Some(position), TileSegment.NE),
        (Some(position), TileSegment.C)
      )
      case TileSegment.S => List(
        (Some(position), TileSegment.SW),
        (Some(position), TileSegment.SE),
        (Some(position), TileSegment.C)
      )
      case TileSegment.E => List(
        (Some(position), TileSegment.NE),
        (Some(position), TileSegment.SE),
        (Some(position), TileSegment.C)
      )
      case TileSegment.W => List(
        (Some(position), TileSegment.NW),
        (Some(position), TileSegment.SW),
        (Some(position), TileSegment.C)
      )
      case TileSegment.NW => List(
        (Some(position), TileSegment.N),
        (Some(position), TileSegment.W)
      )
      case TileSegment.NE => List(
        (Some(position), TileSegment.N),
        (Some(position), TileSegment.E)
      )
      case TileSegment.SW => List(
        (Some(position), TileSegment.W),
        (Some(position), TileSegment.S)
      )
      case TileSegment.SE => List(
        (Some(position), TileSegment.E),
        (Some(position), TileSegment.S)
      )
      case TileSegment.C => List(
        (Some(position), TileSegment.N),
        (Some(position), TileSegment.E),
        (Some(position), TileSegment.S),
        (Some(position), TileSegment.W)
      )
    }
  }
}