package carcassonne.util.adjacency

import carcassonne.model.tile.TileSegment.{C, E, N, NE, NW, S, SE, SW, W}
import carcassonne.model.tile.{SegmentType, TileSegment}
import carcassonne.util.Position

/** Utility class for calculating adjacent positions and segments for calculating connected features.
  */
object ConnectedFeaturesAdjacencyPositions:

  /** Retrieves adjacent tile segments for the given segment and position.
    *
    * @param segment
    *   the segment of the tile
    * @param tilePosition
    *   the position of the tile
    * @param segmentType
    *   the type of the segment
    * @return
    *   a set of positions and segments representing adjacent city tile segments
    */
  def adjacentStrictTileSegments(
      segment: TileSegment,
      tilePosition: Position,
      segmentType: SegmentType
  ): Set[(Position, TileSegment)] =
    segment match
      case NW =>
        Set(
          (tilePosition, N),
          (tilePosition, W),
          (Position(tilePosition.x - 1, tilePosition.y), NE),
          (Position(tilePosition.x, tilePosition.y - 1), SW)
        )
      case N =>
        Set(
          (tilePosition, NW),
          (tilePosition, C),
          (tilePosition, NE),
          (Position(tilePosition.x, tilePosition.y - 1), S)
        )
      case NE =>
        Set(
          (tilePosition, N),
          (tilePosition, E),
          (Position(tilePosition.x + 1, tilePosition.y), NW),
          (Position(tilePosition.x, tilePosition.y - 1), SE)
        )
      case W =>
        Set(
          (tilePosition, NW),
          (tilePosition, C),
          (tilePosition, SW),
          (Position(tilePosition.x - 1, tilePosition.y), E)
        )
      case C =>
        Set(
          (tilePosition, N),
          (tilePosition, W),
          (tilePosition, E),
          (tilePosition, S)
        )
      case E =>
        Set(
          (tilePosition, NE),
          (tilePosition, C),
          (tilePosition, SE),
          (Position(tilePosition.x + 1, tilePosition.y), W)
        )
      case SW =>
        Set(
          (tilePosition, W),
          (tilePosition, S),
          (Position(tilePosition.x - 1, tilePosition.y), SE),
          (Position(tilePosition.x, tilePosition.y + 1), NW)
        )
      case S =>
        Set(
          (tilePosition, SW),
          (tilePosition, C),
          (tilePosition, SE),
          (Position(tilePosition.x, tilePosition.y + 1), TileSegment.N)
        )
      case SE =>
        Set(
          (tilePosition, E),
          (tilePosition, S),
          (Position(tilePosition.x + 1, tilePosition.y), SW),
          (Position(tilePosition.x, tilePosition.y + 1), NE)
        )

  /** Retrieves adjacent segments in diagonal.
    *
    * @param segment
    *   the segment of the tile
    * @param tilePosition
    *   the position of the tile
    * @param segmentType
    *   the type of the segment
    * @return
    *   a set of positions and segments representing adjacent segments within the current tile
    */
  def adjacentSegmentsDiagonal(
      segment: TileSegment,
      tilePosition: Position,
      segmentType: SegmentType
  ): Set[(Position, TileSegment)] =
    segment match
      case NW =>
        Set(
          (Position(tilePosition.x - 1, tilePosition.y - 1), SE),
          (tilePosition, C)
        )
      case N =>
        Set(
          (tilePosition, W),
          (tilePosition, E)
        )
      case NE =>
        Set(
          (Position(tilePosition.x + 1, tilePosition.y - 1), SW),
          (tilePosition, C)
        )
      case W =>
        Set(
          (tilePosition, N),
          (tilePosition, S)
        )
      case C =>
        Set(
          (tilePosition, NE),
          (tilePosition, NE),
          (tilePosition, SE),
          (tilePosition, SE)
        )
      case E =>
        Set(
          (tilePosition, N),
          (tilePosition, S)
        )
      case SW =>
        Set(
          (Position(tilePosition.x - 1, tilePosition.y + 1), NE),
          (tilePosition, C)
        )
      case S =>
        Set(
          (tilePosition, W),
          (tilePosition, E)
        )
      case SE =>
        Set(
          (Position(tilePosition.x + 1, tilePosition.y + 1), NW),
          (tilePosition, C)
        )
