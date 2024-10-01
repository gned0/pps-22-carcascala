package carcassonne.util.adjacency

import carcassonne.model.tile.TileSegment.{C, E, N, NE, NW, S, SE, SW, W}
import carcassonne.model.tile.{SegmentType, TileSegment}
import carcassonne.util.Position

/**
 * Utility class for calculating adjacent positions and segments for calculating connected features.
 */
object ConnectedFeaturesAdjacencyPositions:

  /**
   * Retrieves adjacent road segments within the current tile.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent road segments within the current tile
   */
  def adjacentRoadSegmentsCurrentTile(segment: TileSegment,
                                      tilePosition: Position,
                                       segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case N => Set((tilePosition, C))
      case W => Set((tilePosition, C))
      case C => Set(
          (tilePosition, N), (tilePosition, E),
          (tilePosition, S), (tilePosition, W)
        )
      case E => Set((tilePosition, C))
      case S => Set((tilePosition, C))
      case _ => Set.empty

  /**
   * Retrieves adjacent road segments across tiles.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent road segments across tiles
   */
  def adjacentRoadSegmentsAcrossTiles(segment: TileSegment,
                                      tilePosition: Position,
                                      segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
      case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
      case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
      case S => Set((Position(tilePosition.x, tilePosition.y + 1), N))
      case _ => Set.empty

  /**
   * Retrieves adjacent city segments within the current tile.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent city segments within the current tile
   */
  def adjacentCitySegmentsCurrentTile(segment: TileSegment,
                                      tilePosition: Position,
                                      segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case NW => Set(
          (Position(tilePosition.x - 1, tilePosition.y), NE),
          (Position(tilePosition.x, tilePosition.y - 1), SW)
        )
      case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
      case NE =>
        Set(
          (Position(tilePosition.x + 1, tilePosition.y), NW),
          (Position(tilePosition.x, tilePosition.y - 1), SE)
        )
      case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
      case C => Set.empty
      case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
      case TileSegment.SW =>
        Set(
          (Position(tilePosition.x - 1, tilePosition.y), SE),
          (Position(tilePosition.x, tilePosition.y + 1), NW)
        )
      case S => Set((Position(tilePosition.x, tilePosition.y + 1), TileSegment.N))
      case SE =>
        Set(
          (Position(tilePosition.x + 1, tilePosition.y), SW),
          (Position(tilePosition.x, tilePosition.y + 1), NE)
        )

  /**
   * Retrieves adjacent city segments across tiles.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent city segments across tiles
   */
  def adjacentCitySegmentsAcrossTiles(segment: TileSegment,
                                      tilePosition: Position,
                                      segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case NW => Set((tilePosition, N), (tilePosition, W))
      case N => Set((tilePosition, NW), (tilePosition, C), (tilePosition, NE))
      case NE => Set((tilePosition, N), (tilePosition, E))
      case W => Set((tilePosition, NW), (tilePosition, C), (tilePosition, SW))
      case C => Set((tilePosition, N), (tilePosition, W), (tilePosition, E), (tilePosition, S))
      case E => Set((tilePosition, NE), (tilePosition, C), (tilePosition, SE))
      case SW => Set((tilePosition, W), (tilePosition, S))
      case S => Set((tilePosition, SW), (tilePosition, C), (tilePosition, SE))
      case SE => Set((tilePosition, E), (tilePosition, S))

  /**
   * Retrieves adjacent segments across tiles.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent segments across tiles
   */
  def adjacentSegmentsAcrossTiles(segment: TileSegment,
                                  tilePosition: Position,
                                  segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case NW => Set((tilePosition, N), (tilePosition, C), (tilePosition, W))
      case N => Set((tilePosition, NW), (tilePosition, W), (tilePosition, C),
          (tilePosition, E), (tilePosition, NE)
      )
      case NE => Set((tilePosition, N), (tilePosition, C), (tilePosition, E))
      case W => Set((tilePosition, NW), (tilePosition, N), (tilePosition, C),
          (tilePosition, S), (tilePosition, SW)
      )
      case C => Set((tilePosition, NE), (tilePosition, N), (tilePosition, NE),
          (tilePosition, W), (tilePosition, E), (tilePosition, SE),
          (tilePosition, S), (tilePosition, SE)
      )
      case E => Set((tilePosition, NE), (tilePosition, N), (tilePosition, C),
          (tilePosition, S), (tilePosition, SE)
      )
      case TileSegment.SW => Set((tilePosition, W), (tilePosition, C), (tilePosition, S))
      case S => Set((tilePosition, SW), (tilePosition, W), (tilePosition, C),
          (tilePosition, E), (tilePosition, SE))
      case SE => Set((tilePosition, E), (tilePosition, C), (tilePosition, S))

  /**
   * Retrieves adjacent segments within the current tile.
   *
   * @param segment      the segment of the tile
   * @param tilePosition the position of the tile
   * @param segmentType  the type of the segment
   * @return a set of positions and segments representing adjacent segments within the current tile
   */
  def adjacentSegmentsCurrentTile(segment: TileSegment,
                                  tilePosition: Position,
                                  segmentType: SegmentType): Set[(Position, TileSegment)] =
    segment match
      case NW => Set(
        (Position(tilePosition.x - 1, tilePosition.y - 1), SE),
        (Position(tilePosition.x - 1, tilePosition.y), NE),
        (Position(tilePosition.x, tilePosition.y - 1), SW)
      )
      case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
      case NE =>Set(
        (Position(tilePosition.x + 1, tilePosition.y - 1), SW),
        (Position(tilePosition.x + 1, tilePosition.y), NW),
        (Position(tilePosition.x, tilePosition.y - 1), SE)
      )
      case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
      case C => Set.empty
      case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
      case SW =>Set(
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