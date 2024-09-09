package carcassonne.model.board

import carcassonne.model.*
import carcassonne.model.game.Player
import carcassonne.model.tile.SegmentType.RoadEnd
import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.{Logger, Position}

import scala.collection.mutable

class CarcassonneBoard:
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
      (Position(position.x, position.y - 1), TileSegment.N, TileSegment.S),
      (Position(position.x + 1, position.y), TileSegment.E, TileSegment.W),
      (Position(position.x, position.y + 1), TileSegment.S, TileSegment.N),
      (Position(position.x - 1, position.y), TileSegment.W, TileSegment.E)
    )

    neighborPositions.forall { case (pos, tileSegment, neighborSegment) =>
      board.get(pos).forall { neighborTile =>
        tile.segments(tileSegment) == neighborTile.segments(neighborSegment)
      }
    }

  def getTile(position: Position): Option[GameTile] = board.get(position)

  def getTileMap: Option[Map[Position, GameTile]] =
    if (board.isEmpty) None
    else Some(board.toMap)

  def placeFollower(gameTile: GameTile, segment: TileSegment, player: Player): Boolean =
    if (gameTile.followerMap.contains(segment)) return false

    val connectedFeature = getConnectedFeature(gameTile, segment)

    val isFeatureOccupied = connectedFeature.exists { case (tile, seg) =>
      tile.followerMap.contains(seg)
    }

    if (isFeatureOccupied) return false

    gameTile.followerMap = gameTile.followerMap.updated(segment, player.playerId)
    true

  def getConnectedFeature(startTile: GameTile, startSegment: TileSegment): Set[(GameTile, TileSegment)] =
    var visited: Set[(Position, TileSegment)] = Set.empty
    val result: mutable.Set[(GameTile, TileSegment)] = mutable.Set.empty

    def dfs(position: Position, segment: TileSegment): Unit =
      if (board(position).segments(segment) == RoadEnd) return

      visited += ((position, segment))

      board.get(position).foreach { currentTile =>
        result += ((currentTile, segment))

        val segmentType = currentTile.segments(segment)

        getAdjacentTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
            if (!visited.contains((adjPosition, adjSegment)))
              dfs(adjPosition, adjSegment)
          )
      }

    val startPosition = board.find(_._2 == startTile).map(_._1)
    startPosition.foreach(pos => dfs(pos, startSegment))
    println()
    result.toSet

  private def getAdjacentTileSegments(segment: TileSegment,
                                      tilePosition: Position,
                                      segmentType: SegmentType): Set[(Position, TileSegment)] =
    adjacentSegmentsCurrentTile(segment, tilePosition, segmentType) union
      adjacentSegmentsAcrossTiles(segment, tilePosition, segmentType)

  private def adjacentSegmentsCurrentTile(segment: TileSegment,
                                          tilePosition: Position,
                                          segmentType: SegmentType): Set[(Position, TileSegment)] = {
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
    filterSegmentTypes(adjacencies, segmentType)
  }

  private def adjacentSegmentsAcrossTiles(segment: TileSegment,
                                          tilePosition: Position,
                                          segmentType: SegmentType): Set[(Position, TileSegment)] = {
    val adjacencies: Set[(Position, TileSegment)] = segment match {
      case TileSegment.NW => Set(
        (tilePosition, TileSegment.N),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.W)
      )
      case TileSegment.N => Set(
        (tilePosition, TileSegment.NW),
        (tilePosition, TileSegment.W),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.E),
        (tilePosition, TileSegment.NE)
      )
      case TileSegment.NE => Set(
        (tilePosition, TileSegment.N),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.E)
      )
      case TileSegment.W => Set(
        (tilePosition, TileSegment.NW),
        (tilePosition, TileSegment.N),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.S),
        (tilePosition, TileSegment.SW)
      )
      case TileSegment.C => Set(
        (tilePosition, TileSegment.NE),
        (tilePosition, TileSegment.N),
        (tilePosition, TileSegment.NE),
        (tilePosition, TileSegment.W),
        (tilePosition, TileSegment.E),
        (tilePosition, TileSegment.SE),
        (tilePosition, TileSegment.S),
        (tilePosition, TileSegment.SE)
      )
      case TileSegment.E => Set(
        (tilePosition, TileSegment.NE),
        (tilePosition, TileSegment.N),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.S),
        (tilePosition, TileSegment.SE)
      )
      case TileSegment.SW => Set(
        (tilePosition, TileSegment.W),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.S)
      )
      case TileSegment.S => Set(
        (tilePosition, TileSegment.SW),
        (tilePosition, TileSegment.W),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.E),
        (tilePosition, TileSegment.SE)
      )
      case TileSegment.SE => Set(
        (tilePosition, TileSegment.E),
        (tilePosition, TileSegment.C),
        (tilePosition, TileSegment.S)
      )
    }
    filterSegmentTypes(adjacencies, segmentType)
  }

  private def filterSegmentTypes(adjacencies: Set[(Position, TileSegment)],
                         segmentType: SegmentType): Set[(Position, TileSegment)] =
    adjacencies.filter { case (adjPosition, adjSegment) =>
      getTile(adjPosition).exists(_.segments(adjSegment) == segmentType)
    }