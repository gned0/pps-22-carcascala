package carcassonne.util.adjacency

import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.TileSegment.*
import carcassonne.util.Position

/**
 * Utility class for calculating adjacent positions and segments for scoring purposes.
 */
class ScoreAdjacencyPositions:
  /**
   * Gets the adjacent tiles and segments for a given position and segment.
   *
   * @param position the position of the tile
   * @param segment  the segment of the tile
   * @return a list of adjacent positions and segments
   */
  def getAdjacentCityTilesAndSegments(position: Position,
                                      segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
      case N => List((Some(Position(position.x, position.y - 1)), S))
      case S => List((Some(Position(position.x, position.y + 1)), N))
      case E => List((Some(Position(position.x + 1, position.y)), W))
      case W => List((Some(Position(position.x - 1, position.y)), E))
      case NE => List(
        (Some(Position(position.x, position.y - 1)), S),
        (Some(Position(position.x + 1, position.y)), W)
      )
      case NW => List(
        (Some(Position(position.x, position.y - 1)), S),
        (Some(Position(position.x - 1, position.y)), E)
      )
      case SE => List(
        (Some(Position(position.x, position.y + 1)), N),
        (Some(Position(position.x + 1, position.y)), W)
      )
      case SW => List(
        (Some(Position(position.x, position.y + 1)), N),
        (Some(Position(position.x - 1, position.y)), E)
      )
      case _ => List()

  /**
   * Gets the adjacent road tiles and segments for a given position and segment.
   *
   * @param position the position of the tile
   * @param segment  the segment of the tile
   * @return a list of adjacent positions and segments
   */
  def getAdjacentRoadTilesAndSegments(position: Position,
                                      segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
      case N => List((Some(Position(position.x, position.y - 1)), S))
      case S => List((Some(Position(position.x, position.y + 1)), N))
      case E => List((Some(Position(position.x + 1, position.y)), W))
      case W => List((Some(Position(position.x - 1, position.y)), E))
      case _ => List()

  /**
   * Gets the adjacent field segments for a given position and segment.
   *
   * @param position the position of the tile
   * @param segment  the segment of the tile
   * @return a list of adjacent positions and segments
   */
  def getAdjacentFieldSegments(position: Position,
                               segment: TileSegment): List[(Option[Position], TileSegment)] =
    segment match
      case N => List(
        (Some(position), NW),
        (Some(position), NE),
        (Some(position), C)
      )
      case S => List(
        (Some(position), SW),
        (Some(position), SE),
        (Some(position), C)
      )
      case E => List(
        (Some(position), NE),
        (Some(position), SE),
        (Some(position), C)
      )
      case W => List(
        (Some(position), NW),
        (Some(position), SW),
        (Some(position), C)
      )
      case NW => List(
        (Some(position), N),
        (Some(position), W)
      )
      case NE => List(
        (Some(position), N),
        (Some(position), E)
      )
      case SW => List(
        (Some(position), W),
        (Some(position), S)
      )
      case SE => List(
        (Some(position), E),
        (Some(position), S)
      )
      case C => List(
        (Some(position), N),
        (Some(position), E),
        (Some(position), S),
        (Some(position), W)
      )
