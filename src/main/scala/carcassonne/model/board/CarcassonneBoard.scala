package carcassonne.model.board

import carcassonne.model.*
import carcassonne.model.game.Player
import carcassonne.model.tile.SegmentType.{City, Road, RoadEnd}
import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.TileSegment._
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.{Logger, Position}

import scala.collection.mutable

trait CarcassonneBoard:
  def placeTile(tile: GameTile, position: Position): Boolean
  def getTile(position: Position): Option[GameTile]
  def getTileMap: Option[Map[Position, GameTile]]
  def placeFollower(position: Position, segment: TileSegment, player: Player): Boolean
  def removeFollower(gameTile: GameTile): Boolean
  def getConnectedFeature(startPosition: Position, startSegment: TileSegment): Set[(Position, TileSegment)]

object CarcassonneBoard:
  def apply(): CarcassonneBoard = new CarcassonneBoardImpl()

  private class CarcassonneBoardImpl extends CarcassonneBoard:
    private val board: mutable.Map[Position, GameTile] = mutable.Map.empty

    def placeTile(tile: GameTile, position: Position): Boolean =
      if (board.contains(position)) throw IllegalArgumentException(s"Tile already placed at position $position")
      else if (isValidTilePlacement(tile, position))
        board(position) = tile
        Logger.log("MODEL", s"Tile placed at $position")
        true
      else
        throw IllegalArgumentException(s"Invalid tile placement at position $position")

    private def isValidTilePlacement(tile: GameTile, position: Position): Boolean =
      val neighborPositions = List(
        (Position(position.x, position.y - 1), N, S),
        (Position(position.x + 1, position.y), E, W),
        (Position(position.x, position.y + 1), S, N),
        (Position(position.x - 1, position.y), W, E)
      )

      neighborPositions.forall { case (pos, tileSegment, neighborSegment) =>
        board.get(pos).forall { neighborTile =>
          tile.segments(tileSegment) == neighborTile.segments(neighborSegment)
        }
      }
    
    def getPosition(tile: GameTile): Option[Position] = board.find(_._2 == tile).map(_._1)

    def getTileMap: Option[Map[Position, GameTile]] =
      if (board.isEmpty) None
      else Some(board.toMap)
    
    def getTile(position: Position): Option[GameTile] = board.get(position)

    def placeFollower(position: Position, segment: TileSegment, player: Player): Boolean =
      if (getTile(position).isDefined && getTile(position).get.followerMap.contains(segment)) return false

      val connectedFeature = getConnectedFeature(position, segment)

      val isFeatureOccupied = connectedFeature.exists { case (pos, seg) =>
        getTile(pos).get.followerMap.contains(seg)
      }

      if (isFeatureOccupied) return false

      getTile(position).get.followerMap = getTile(position).get.followerMap.updated(segment, player.playerId)
      true

    def removeFollower(gameTile: GameTile): Boolean =
      gameTile.followerMap = Map.empty
      true

    def getConnectedFeature(startPosition: Position, startSegment: TileSegment): Set[(Position, TileSegment)] =
      var visited: Set[(Position, TileSegment)] = Set.empty
      val result: mutable.Set[(Position, TileSegment)] = mutable.Set.empty

      def dfs(position: Position, segment: TileSegment): Unit =
        if (!board.contains(position) || board(position).segments(segment) == RoadEnd) return

        visited += ((position, segment))

        board.get(position).foreach { currentTile =>
          result += ((position, segment))

          val segmentType = currentTile.segments(segment)

          segmentType match
            case Road =>
              getAdjacentRoadTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if (!visited.contains((adjPosition, adjSegment)))
                  dfs(adjPosition, adjSegment)
              )
            case City =>
              getAdjacentCityTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if (!visited.contains((adjPosition, adjSegment)))
                  dfs(adjPosition, adjSegment)
              )
            case _ =>
              getAdjacentTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if (!visited.contains((adjPosition, adjSegment)))
                  dfs(adjPosition, adjSegment)
              )
        }

      dfs(startPosition, startSegment)
      result.toSet

    private def getAdjacentCityTileSegments(segment: TileSegment,
                                            tilePosition: Position,
                                            segmentType: SegmentType): Set[(Position, TileSegment)] =
      adjacentCitySegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentCitySegmentsAcrossTiles(segment, tilePosition, segmentType)

    private def adjacentCitySegmentsCurrentTile(segment: TileSegment,
                                                tilePosition: Position,
                                                segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case NW => Set(
          (Position(tilePosition.x - 1, tilePosition.y), NE),
          (Position(tilePosition.x, tilePosition.y - 1), SW)
        )
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case NE => Set(
          (Position(tilePosition.x + 1, tilePosition.y), NW),
          (Position(tilePosition.x, tilePosition.y - 1), SE),
        )
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case C => Set.empty
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case TileSegment.SW => Set(
          (Position(tilePosition.x - 1, tilePosition.y), SE),
          (Position(tilePosition.x, tilePosition.y + 1), NW)
        )
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), TileSegment.N))
        case SE => Set(
          (Position(tilePosition.x + 1, tilePosition.y), SW),
          (Position(tilePosition.x, tilePosition.y + 1), NE)
        )
      }
      filterSegmentTypes(adjacencies, segmentType)
    }

    private def adjacentCitySegmentsAcrossTiles(segment: TileSegment,
                                                tilePosition: Position,
                                                segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case NW => Set(
          (tilePosition, N),
          (tilePosition, W)
        )
        case N => Set(
          (tilePosition, NW),
          (tilePosition, C),
          (tilePosition, NE)
        )
        case NE => Set(
          (tilePosition, N),
          (tilePosition, E)
        )
        case W => Set(
          (tilePosition, NW),
          (tilePosition, C),
          (tilePosition, SW)
        )
        case C => Set(
          (tilePosition, N),
          (tilePosition, W),
          (tilePosition, E),
          (tilePosition, S)
        )
        case E => Set(
          (tilePosition, NE),
          (tilePosition, C),
          (tilePosition, SE)
        )
        case SW => Set(
          (tilePosition, W),
          (tilePosition, S)
        )
        case S => Set(
          (tilePosition, SW),
          (tilePosition, C),
          (tilePosition, SE)
        )
        case SE => Set(
          (tilePosition, E),
          (tilePosition, S)
        )
      }
      filterSegmentTypes(adjacencies, segmentType)
    }


    private def getAdjacentRoadTileSegments(segment: TileSegment,
                                            tilePosition: Position,
                                            segmentType: SegmentType): Set[(Position, TileSegment)] =
      adjacentRoadSegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentRoadSegmentsAcrossTiles(segment, tilePosition, segmentType)

    private def adjacentRoadSegmentsCurrentTile(segment: TileSegment,
                                                tilePosition: Position,
                                                segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case N => Set((tilePosition, C))
        case W => Set((tilePosition, C))
        case C => Set(
          (tilePosition, N),
          (tilePosition, E),
          (tilePosition, S),
          (tilePosition, W),
        )
        case E => Set((tilePosition, C))
        case S => Set((tilePosition, C))
        case _ => Set.empty
      }
      filterSegmentTypes(adjacencies, segmentType)
    }

    private def adjacentRoadSegmentsAcrossTiles(segment: TileSegment,
                                                tilePosition: Position,
                                                segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), N))
        case _ => Set.empty
      }
      filterSegmentTypes(adjacencies, segmentType)
    }

    private def getAdjacentTileSegments(segment: TileSegment,
                                        tilePosition: Position,
                                        segmentType: SegmentType): Set[(Position, TileSegment)] =
      adjacentSegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentSegmentsAcrossTiles(segment, tilePosition, segmentType)

    private def adjacentSegmentsCurrentTile(segment: TileSegment,
                                            tilePosition: Position,
                                            segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case NW => Set(
          (Position(tilePosition.x - 1, tilePosition.y - 1), SE),
          (Position(tilePosition.x - 1, tilePosition.y), NE),
          (Position(tilePosition.x, tilePosition.y - 1), SW)
        )
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case NE => Set(
          (Position(tilePosition.x + 1, tilePosition.y - 1), SW),
          (Position(tilePosition.x + 1, tilePosition.y), NW),
          (Position(tilePosition.x, tilePosition.y - 1), SE),
        )
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case C => Set.empty
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case SW => Set(
          (Position(tilePosition.x - 1, tilePosition.y + 1), NE),
          (Position(tilePosition.x - 1, tilePosition.y), SE),
          (Position(tilePosition.x, tilePosition.y + 1), NW)
        )
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), N))
        case SE => Set(
          (Position(tilePosition.x + 1, tilePosition.y + 1), NW),
          (Position(tilePosition.x + 1, tilePosition.y), SW),
          (Position(tilePosition.x, tilePosition.y + 1), NE)
        )
      }
      filterSegmentTypes(adjacencies, segmentType)
    }

    private def adjacentSegmentsAcrossTiles(segment: TileSegment,
                                            tilePosition: Position,
                                            segmentType: SegmentType): Set[(Position, TileSegment)] = {
      val adjacencies: Set[(Position, TileSegment)] = segment match {
        case NW => Set(
          (tilePosition, N),
          (tilePosition, C),
          (tilePosition, W)
        )
        case N => Set(
          (tilePosition, NW),
          (tilePosition, W),
          (tilePosition, C),
          (tilePosition, E),
          (tilePosition, NE)
        )
        case NE => Set(
          (tilePosition, N),
          (tilePosition, C),
          (tilePosition, E)
        )
        case W => Set(
          (tilePosition, NW),
          (tilePosition, N),
          (tilePosition, C),
          (tilePosition, S),
          (tilePosition, SW)
        )
        case C => Set(
          (tilePosition, NE),
          (tilePosition, N),
          (tilePosition, NE),
          (tilePosition, W),
          (tilePosition, E),
          (tilePosition, SE),
          (tilePosition, S),
          (tilePosition, SE)
        )
        case E => Set(
          (tilePosition, NE),
          (tilePosition, N),
          (tilePosition, C),
          (tilePosition, S),
          (tilePosition, SE)
        )
        case TileSegment.SW => Set(
          (tilePosition, W),
          (tilePosition, C),
          (tilePosition, S)
        )
        case S => Set(
          (tilePosition, SW),
          (tilePosition, W),
          (tilePosition, C),
          (tilePosition, E),
          (tilePosition, SE)
        )
        case SE => Set(
          (tilePosition, E),
          (tilePosition, C),
          (tilePosition, S)
        )
      }
      filterSegmentTypes(adjacencies, segmentType)
    }

    private def filterSegmentTypes(adjacencySet: Set[(Position, TileSegment)],
                                   segmentType: SegmentType): Set[(Position, TileSegment)] =
      adjacencySet.filter { case (adjPosition, adjSegment) =>
        getTile(adjPosition).exists(_.segments(adjSegment) == segmentType)
      }