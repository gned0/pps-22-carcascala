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
   * @param position The position of the tile on the board.
   * @param map The current state of the Carcassonne board.
   * @return The total points for the city.
   */
  def calculateCityPoints(followerSegment: TileSegment,
                          position: Position,
                          map: CarcassonneBoard,
                          endGame: Boolean): Int =
    if endGame then
      cityPointsCalculation(followerSegment, position, map)
    else
      cityPointsCalculation(followerSegment, position, map) * 2

  private def cityPointsCalculation(meepleSegment: TileSegment,
                                    position: Position,
                                    map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    if isCityFinished(connectedFeatures, map) then
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
                          map: CarcassonneBoard): Int =
    roadPointsCalculation(followerSegment, position, map)

  private def roadPointsCalculation(meepleSegment: TileSegment,
                                    position: Position,
                                    map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    if isRoadFinished(connectedFeatures, map) then
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
                          map: CarcassonneBoard): Int =
    monasteryPointsCalculation(followerSegment, position, map) * 9

  private def monasteryPointsCalculation(meepleSegment: TileSegment,
                                    position: Position,
                                    map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, meepleSegment)
    if isMonasteryFinished(connectedFeatures, map) then
      val uniqueTiles: Set[(Position, TileSegment)] = connectedFeatures
        .groupBy(_._1)
        .map { case (gameTile, tuples) => tuples.head }
        .toSet
      uniqueTiles.size
    else
      0

  private def isMonasteryFinished(connectedFeatures: Set[(Position, TileSegment)],
                                  map: CarcassonneBoard): Boolean =
    val monasteryPosition = connectedFeatures.head._1
    for i <- -1 to 1 do
      for j <- -1 to 1 do
        if i != 0 || j != 0 then
          val pos = Position(monasteryPosition.x + i, monasteryPosition.y + j)
          if map.getTile(pos).isEmpty then
            return false
    true





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












  /**
   * Provides a map of direct adjacencies for each TileSegment.
   */
  private val adjacencyMap: Map[TileSegment, Set[TileSegment]] = Map(
    TileSegment.NW -> Set(TileSegment.N, TileSegment.W, TileSegment.C),
    TileSegment.N -> Set(TileSegment.NW, TileSegment.NE, TileSegment.C),
    TileSegment.NE -> Set(TileSegment.N, TileSegment.E, TileSegment.C),
    TileSegment.W -> Set(TileSegment.NW, TileSegment.SW, TileSegment.C),
    TileSegment.C -> Set(TileSegment.NW, TileSegment.N, TileSegment.NE, TileSegment.W, TileSegment.E, TileSegment.SW, TileSegment.S, TileSegment.SE),
    TileSegment.E -> Set(TileSegment.NE, TileSegment.SE, TileSegment.C),
    TileSegment.SW -> Set(TileSegment.W, TileSegment.S, TileSegment.C),
    TileSegment.S -> Set(TileSegment.SW, TileSegment.SE, TileSegment.C),
    TileSegment.SE -> Set(TileSegment.S, TileSegment.E, TileSegment.C)
  )

  /**
   * Finds adjacent field segments within the same tile.
   *
   * @param segment The segment of the tile to check.
   * @param gameTile The tile to check for adjacent field segments.
   * @return A set of adjacent field segments.
   */
  private def adjacentFieldSegments(segment: TileSegment,
                                    gameTile: GameTile): Set[TileSegment] = {

    adjacencyMap(segment).filter { adjacentSegment =>
      gameTile.segments(adjacentSegment) == gameTile.segments(segment) && gameTile.segments(adjacentSegment) == SegmentType.Field
    }
  }

  /**
   * Finds adjacent field segments across different tiles.
   *
   * @param segment The segment of the tile to check.
   * @param tilePosition The position of the tile on the board.
   * @param map The current state of the Carcassonne board.
   * @return A set of adjacent field segments across different tiles.
   */
  private def adjacentFieldSegmentsAcrossTiles(segment: TileSegment,
                                               tilePosition: Position,
                                               map: CarcassonneBoard): Set[(Position, TileSegment)] = {
    val adjacencies: Set[(Position, TileSegment)] = segment match {
      case TileSegment.NW => Set(
        (Position(tilePosition.x - 1, tilePosition.y - 1), TileSegment.SE),
        (Position(tilePosition.x - 1, tilePosition.y), TileSegment.NE),
        (Position(tilePosition.x, tilePosition.y - 1), TileSegment.SW)
      )
      case TileSegment.N => Set((Position(tilePosition.x, tilePosition.y - 1), TileSegment.S))
      case TileSegment.NE => Set(
        (Position(tilePosition.x + 1, tilePosition.y - 1), TileSegment.SW),
        (Position(tilePosition.x + 1, tilePosition.y), TileSegment.NW),
        (Position(tilePosition.x, tilePosition.y - 1), TileSegment.SE),
      )
      case TileSegment.W => Set((Position(tilePosition.x - 1, tilePosition.y), TileSegment.E))
      case TileSegment.C => Set.empty
      case TileSegment.E => Set((Position(tilePosition.x + 1, tilePosition.y), TileSegment.W))
      case TileSegment.SW => Set(
        (Position(tilePosition.x - 1, tilePosition.y + 1), TileSegment.NE),
        (Position(tilePosition.x - 1, tilePosition.y), TileSegment.SE),
        (Position(tilePosition.x, tilePosition.y + 1), TileSegment.NW)
      )
      case TileSegment.S => Set((Position(tilePosition.x, tilePosition.y + 1), TileSegment.N))
      case TileSegment.SE => Set(
        (Position(tilePosition.x + 1, tilePosition.y + 1), TileSegment.NW),
        (Position(tilePosition.x + 1, tilePosition.y), TileSegment.SW),
        (Position(tilePosition.x, tilePosition.y + 1), TileSegment.NE)
      )
    }


    adjacencies.filter { case (adjPosition, adjSegment) =>

      map.getTile(adjPosition).exists(_.segments(adjSegment) == SegmentType.Field)
    }
  }

  /**
   * Checks if a segment is adjacent to a city.
   *
   * @param segment The segment of the tile to check.
   * @param tilePosition The position of the tile on the board.
   * @param gameTile The tile to check for adjacent city segments.
   * @param map The current state of the Carcassonne board.
   * @return A set of adjacent city segments.
   */
  private def isAdjacentToCity(segment: TileSegment,
                               tilePosition: Position,
                               gameTile: GameTile,
                               map: CarcassonneBoard): Set[(Position, TileSegment)] = {
    val intraTileAdjacentCities = adjacencyMap(segment).flatMap { adjSegment =>
      if (gameTile.segments.get(adjSegment).contains(SegmentType.City))
        Some((tilePosition, adjSegment))
      else
        None
    }

    val interTileAdjacencies = segment match {
      case TileSegment.NW => Set((Position(tilePosition._1 - 1, tilePosition._2 - 1), TileSegment.SE))
      case TileSegment.N => Set((Position(tilePosition._1, tilePosition._2 - 1), TileSegment.S))
      case TileSegment.NE => Set((Position(tilePosition._1 + 1, tilePosition._2 - 1), TileSegment.SW))
      case TileSegment.W => Set((Position(tilePosition._1 - 1, tilePosition._2), TileSegment.E))
      case TileSegment.C => Set.empty
      case TileSegment.E => Set((Position(tilePosition._1 + 1, tilePosition._2), TileSegment.W))
      case TileSegment.SW => Set((Position(tilePosition._1 - 1, tilePosition._2 + 1), TileSegment.NE))
      case TileSegment.S => Set((Position(tilePosition._1, tilePosition._2 + 1), TileSegment.N))
      case TileSegment.SE => Set((Position(tilePosition._1 + 1, tilePosition._2 + 1), TileSegment.NW))
    }

    val interTileAdjacentCities = interTileAdjacencies.flatMap { case (adjPosition, adjSegment) =>
      map.getTile(adjPosition).flatMap { adjTile =>
        if (adjTile.segments.get(adjSegment).contains(SegmentType.City))
          Some((adjPosition, adjSegment))
        else
          None
      }
    }
    intraTileAdjacentCities ++ interTileAdjacentCities
  }

  /**
   * Checks if the given city segments are part of the same city.
   *
   * @param citySegments The set of city segments to check.
   * @param map The current state of the Carcassonne board.
   * @return True if the segments are part of the same city, false otherwise.
   */
  private def areSegmentsPartOfSameCity(citySegments: Set[(Position, TileSegment)],
                                        map: CarcassonneBoard): Boolean = {
    if (citySegments.size <= 1) return true

    val visited = mutable.Set[(Position, TileSegment)]()
    val stack = mutable.Stack[(Position, TileSegment)]()
    val startSegment = citySegments.head
    stack.push(startSegment)
    visited.add(startSegment)

    while (stack.nonEmpty) {
      val (currentPos, currentSegment) = stack.pop()
      val adjacentCities = isAdjacentToCity(currentSegment, currentPos, map.getTile(currentPos).get, map)
      val validAdjacentCities = adjacentCities.intersect(citySegments)

      validAdjacentCities.foreach { adjCity =>
        if (!visited.contains(adjCity)) {
          visited.add(adjCity)
          stack.push(adjCity)
        }
      }
    }
    visited.size == citySegments.size
  }

  /**
   * Recursively calculates the points for a field.
   *
   * @param followerSegment The segment of the tile where the meeple is placed.
   * @param position The position of the tile on the board.
   * @param map The current state of the Carcassonne board.
   * @return The total points for the field.
   */
  private def recursiveFieldPointsCalculation(followerSegment: TileSegment,
                                              position: Position,
                                              map: CarcassonneBoard): Int =
    var citiesToCheck: List[(TileSegment, Position)] = List.empty
    var fieldsVisited: List[(TileSegment, Position)] = List.empty

    def exploreField(followerSegment: TileSegment, position: Position): Unit =
      fieldsVisited = fieldsVisited :+ (followerSegment, position)

      val currentTile = map.getTile(position).get

      for adjSegment <- adjacentFieldSegments(followerSegment, currentTile) do
        if !fieldsVisited.contains((adjSegment, position)) then
          exploreField(adjSegment, position)

      for (adjPosition, adjSegment) <- adjacentFieldSegmentsAcrossTiles(followerSegment, position, map) do
        if map.getTileMap.get.contains(adjPosition) && !fieldsVisited.contains(adjSegment, Position(adjPosition.x, adjPosition.y)) then
          exploreField(adjSegment, adjPosition)

      isAdjacentToCity(followerSegment, position, currentTile, map).foreach((pos, seg) => if !citiesToCheck.contains((seg, pos)) then citiesToCheck = citiesToCheck :+ (seg, pos))

    exploreField(followerSegment, position)

    val combinations = (2 to citiesToCheck.length).flatMap(citiesToCheck.combinations).
      filter(list => areSegmentsPartOfSameCity(list.map((seg, pos) => (pos, seg)).toSet, map)).
      map(_.init).filter(_.nonEmpty).flatten

    val results = citiesToCheck.filter((seg, pos) => !combinations.contains(seg, pos) &&
      calculateCityPoints(seg, pos, map, false) != 0)

    results.size * 3

  /**
   * Recursively calculates the points for a road.
   *
   * @param followerSegment The segment of the tile where the meeple is placed.
   * @param position The position of the tile on the board.
   * @param map The current state of the Carcassonne board.
   * @return The total points for the road.
   */
  private def recursiveRoadPointsCalculation(followerSegment: TileSegment,
                                             position: Position,
                                             map: CarcassonneBoard): Int =
    val originalPos = position
    var exploredPositions: List[Position] = List.empty

    @tailrec
    def helper(segmentsToCheck: List[(TileSegment, Position)], acc: Int): Int =
      segmentsToCheck match
        case Nil => acc
        case (segment, pos) :: tail =>
          val tilesToCheck: List[(TileSegment, Position)] =
            List(
              (TileSegment.N, Position(pos.x, pos.y - 1)),
              (TileSegment.S, Position(pos.x, pos.y + 1)),
              (TileSegment.W, Position(pos.x - 1, pos.y)),
              (TileSegment.E, Position(pos.x + 1, pos.y))
            ).filter { case (seg, checkPos) =>
              map.getTileMap.get.get(pos).exists(_.segments(seg) == SegmentType.Road
                && !exploredPositions.contains(checkPos))
            }.map((seg, pos) => seg match
              case TileSegment.N => (TileSegment.S, pos)
              case TileSegment.S => (TileSegment.N, pos)
              case TileSegment.W => (TileSegment.E, pos)
              case TileSegment.E => (TileSegment.W, pos)
            )

          tilesToCheck.foldLeft((tail, acc)) { case ((remainingSegments, currentAcc), (seg, checkTilePosition)) =>
            exploredPositions = exploredPositions :+ checkTilePosition
            map.getTileMap.get.get(checkTilePosition) match
              case Some(tile) if tile.segments(seg) == SegmentType.Road
                && tile.segments(TileSegment.C) == SegmentType.RoadEnd =>
                (remainingSegments, currentAcc + 1)
              case Some(tile) if checkTilePosition == originalPos =>
                (remainingSegments, currentAcc)
              case Some(tile) =>
                val newSegments = tile.segments.collect {
                  case (newSeg, SegmentType.Road) if List(TileSegment.N,
                    TileSegment.S,
                    TileSegment.W,
                    TileSegment.E).contains(newSeg) && newSeg != seg =>
                    (newSeg, checkTilePosition)
                }.toList
                (newSegments ++ remainingSegments, currentAcc + 1)
              case None =>
                (remainingSegments, currentAcc)
          } match {
            case (newSegments, newAcc) => helper(newSegments, newAcc)
          }
    helper(List((followerSegment, position)), 0)

  /**
   * Recursively calculates the points for a city.
   *
   * @param followerSegment The segment of the tile where the meeple is placed.
   * @param position The position of the tile on the board.
   * @param map The current state of the Carcassonne board.
   * @return The total points for the city.
   */
  private def recursiveCityPointsCalculation(followerSegment: TileSegment,
                                             position: Position,
                                             map: CarcassonneBoard): Int =
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

    helper(List((followerSegment, position)), 0)
}