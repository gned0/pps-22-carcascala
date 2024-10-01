package carcassonne.util

import carcassonne.model.tile.TileSegment

class AdjacencyPositions:
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
      case TileSegment.N => List((Some(Position(position.x, position.y - 1)), TileSegment.S))
      case TileSegment.S => List((Some(Position(position.x, position.y + 1)), TileSegment.N))
      case TileSegment.E => List((Some(Position(position.x + 1, position.y)), TileSegment.W))
      case TileSegment.W => List((Some(Position(position.x - 1, position.y)), TileSegment.E))
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