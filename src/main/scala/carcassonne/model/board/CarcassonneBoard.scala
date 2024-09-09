package carcassonne.model.board

import carcassonne.model.*
import carcassonne.model.game.Player
import carcassonne.model.tile.TileSegment.{N, NE, adjacentSegments}
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
      if (visited.contains((position, segment))) return

      visited += ((position, segment))

      board.get(position).foreach { currentTile =>
        result += ((currentTile, segment))

        val segmentType = currentTile.segments(segment)

        // Check connected segments on the same tile
        currentTile.segments.foreach { case (seg, segType) =>
          if (segType == segmentType && seg != segment && adjacentSegments(segment).contains(seg))
            dfs(position, seg)
        }

        // Check neighboring tiles
        getNeighborTiles(position, segment).foreach { case (neighborPos, neighborSegment) =>
          board.get(neighborPos).foreach { neighborTile =>
            if (neighborTile.segments(neighborSegment) == segmentType)
              dfs(neighborPos, neighborSegment)
          }
        }
      }

    val startPosition = board.find(_._2 == startTile).map(_._1)
    startPosition.foreach(pos => dfs(pos, startSegment))
    result.toSet

  private def getNeighborPositions(position: Position): List[(Position, List[TileSegment])] =
    List(
      (Position(position.x, position.y - 1), List(TileSegment.SW, TileSegment.S, TileSegment.SE)), // west
      (Position(position.x + 1, position.y), List(TileSegment.NW, TileSegment.W, TileSegment.SW)), // south
      (Position(position.x, position.y + 1), List(TileSegment.NW, TileSegment.N, TileSegment.NE)), // east
      (Position(position.x - 1, position.y), List(TileSegment.NE, TileSegment.E, TileSegment.SE)) // north
    )

  private def getNeighborTiles(position: Position, tileSegment: TileSegment): List[(Position, TileSegment)] =
    tileSegment match
      case TileSegment.N => List((Position(position.x - 1, position.y), TileSegment.S))
      case TileSegment.E => List((Position(position.x, position.y + 1), TileSegment.W))
      case TileSegment.S => List((Position(position.x + 1, position.y), TileSegment.N))
      case TileSegment.W => List((Position(position.x, position.y - 1), TileSegment.E))
      case TileSegment.NE => List((Position(position.x - 1, position.y), TileSegment.SE),
                                  (Position(position.x, position.y + 1), TileSegment.NW))
      case TileSegment.NW => List((Position(position.x - 1, position.y), TileSegment.SW),
                                    (Position(position.x, position.y - 1), TileSegment.NE))
      case TileSegment.SE => List((Position(position.x + 1, position.y), TileSegment.NE),
                                    (Position(position.x, position.y + 1), TileSegment.SW))
      case TileSegment.SW => List((Position(position.x + 1, position.y), TileSegment.NW),
                                    (Position(position.x, position.y - 1), TileSegment.SE))
      case _ => List.empty


