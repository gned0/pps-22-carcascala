package carcassonne.model.game

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.Position

class ScoreCalculator:

  def calculateCityPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val points = cityPointsCalculation(followerSegment, position, map, endGame)
    if endGame then points else points * 2

  private def cityPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    if isCityFinished(connectedFeatures, map) || endGame then
      connectedFeatures.groupBy(_._1).map(_._2.head).toSet.size
    else 0

  private def isCityFinished(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Boolean =
    connectedFeatures.forall { (pos, segment) =>
      getAdjacentTilesAndSegments(pos, segment).forall {
        case (Some(connectedPos), adjSegment) =>
          map.getTile(connectedPos).exists(_.segments(adjSegment) == SegmentType.City)
        case _ => false
      }
    }

  private def getAdjacentTilesAndSegments(position: Position, segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
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

  def calculateRoadPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    roadPointsCalculation(followerSegment, position, map, endGame)

  private def roadPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    if isRoadFinished(connectedFeatures, map) || endGame then
      connectedFeatures.groupBy(_._1).map(_._2.head).toSet.size
    else 0

  private def isRoadFinished(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Boolean =
    connectedFeatures.forall { (tile, segment) =>
      getAdjacentRoadTilesAndSegments(tile, segment).forall {
        case (Some(connectedPos), adjSegment) =>
          map.getTile(connectedPos).exists(_.segments(adjSegment) == SegmentType.Road)
        case _ => false
      }
    }

  private def getAdjacentRoadTilesAndSegments(position: Position, segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
      case TileSegment.N => List((Some(Position(position.x, position.y - 1)), TileSegment.S))
      case TileSegment.S => List((Some(Position(position.x, position.y + 1)), TileSegment.N))
      case TileSegment.E => List((Some(Position(position.x + 1, position.y)), TileSegment.W))
      case TileSegment.W => List((Some(Position(position.x - 1, position.y)), TileSegment.E))
      case _ => List()

  def calculateMonasteryPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    monasteryPointsCalculation(followerSegment, position, map, endGame)

  private def monasteryPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard, endGame: Boolean): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    val monasterySurroundings = calculateMonasterySurroundings(connectedFeatures, map)
    if monasterySurroundings == 9 || endGame then monasterySurroundings else 0

  private def calculateMonasterySurroundings(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Int =
    val monasteryPosition = connectedFeatures.head._1
    (for
      i <- -1 to 1
      j <- -1 to 1
      pos = Position(monasteryPosition.x + i, monasteryPosition.y + j)
      if map.getTile(pos).isDefined
    yield pos).size

  def calculateFieldPoints(followerSegment: TileSegment, position: Position, map: CarcassonneBoard): Int =
    fieldPointsCalculation(followerSegment, position, map)

  private def fieldPointsCalculation(followerSegment: TileSegment, position: Position, map: CarcassonneBoard): Int =
    val connectedFeatures = map.getConnectedFeature(position, followerSegment)
    getCitiesNextToFields(connectedFeatures, map) * 3

  private def getCitiesNextToFields(connectedFeatures: Set[(Position, TileSegment)], map: CarcassonneBoard): Int =
    var result = 0
    var visited: Set[(Position, TileSegment)] = Set.empty
    for (tile, segment) <- connectedFeatures do
      for (adjPos, adjSegment) <- getAdjacentFieldSegments(tile, segment) do
        adjPos match
          case Some(connectedPos) if map.getTile(connectedPos).nonEmpty =>
            map.getTile(connectedPos).get.segments(adjSegment) match
              case SegmentType.City =>
                val connectedCityFeatures = map.getConnectedFeature(connectedPos, adjSegment)
                if connectedCityFeatures.intersect(visited).isEmpty then
                  visited ++= connectedCityFeatures
                  if isCityFinished(connectedCityFeatures, map) then result += 1
              case _ =>
    result

  private def getAdjacentFieldSegments(position: Position, segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
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