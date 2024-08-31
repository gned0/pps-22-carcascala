package carcassonne.model.board

import carcassonne.model.*
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.util.Logger

/**
 * Represents the game map, which holds the placed tiles and manages the graph of tile connections.
 *
 * This class extends `GameBoard[GameTile]` and `Graph[Position]`, meaning it can manage a board of tiles
 * and handle graph-related operations for connected regions.
 */
class CarcassonneBoard extends GameBoard[GameTile] with Graph[Position]:

  /**
   * Places a tile on the map at the specified position and updates the graph structure.
   *
   * @param tile The `GameTile` to place.
   * @param position The `Position` where the tile should be placed.
   * @throws IllegalArgumentException if a tile is already placed at the position or if the placement is invalid.
   */
  def placeTile(tile: GameTile, position: Position): Boolean =
    if getElement(position).isDefined then
      throw IllegalArgumentException(s"Tile already placed at position $position")

    if isValidPlacement(tile, position) then
      placeElement(tile, position)
      Logger.log("MODEL", s"Tile placed at $position")

      addNode(position)

      updateGraphEdges(tile, position)
      true
    else
      throw IllegalArgumentException(s"Invalid tile placement at position $position")

  /**
   * Updates the graph edges based on the neighboring positions and tile segments.
   *
   * @param tile The `GameTile` that has just been placed.
   * @param position The `Position` where the tile is placed.
   */
  private def updateGraphEdges(tile: GameTile, position: Position): Unit =
    val neighbors = List(
      (Position(position.x, position.y - 1), TileSegment.N, TileSegment.S), // North neighbor
      (Position(position.x + 1, position.y), TileSegment.E, TileSegment.W), // East neighbor
      (Position(position.x, position.y + 1), TileSegment.S, TileSegment.N), // South neighbor
      (Position(position.x - 1, position.y), TileSegment.W, TileSegment.E)  // West neighbor
    )

    neighbors.foreach { case (neighborPos, tileSegment, neighborSegment) =>
      getElement(neighborPos).foreach { neighborTile =>
        if tile.segments(tileSegment) == neighborTile.segments(neighborSegment) then
          addEdge(position, neighborPos)
      }
    }

  /**
   * Validates whether a tile can be placed at the specified position.
   *
   * @param tile The `GameTile` to validate.
   * @param position The `Position` where the tile is to be placed.
   * @return `true` if the placement is valid, `false` otherwise.
   */
  private def isValidPlacement(tile: GameTile, position: Position): Boolean =
    val neighbors = List(
      (Position(position.x, position.y - 1), TileSegment.N, TileSegment.S), // North neighbor
      (Position(position.x + 1, position.y), TileSegment.E, TileSegment.W), // East neighbor
      (Position(position.x, position.y + 1), TileSegment.S, TileSegment.N), // South neighbor
      (Position(position.x - 1, position.y), TileSegment.W, TileSegment.E)  // West neighbor
    )

    neighbors.forall { case (pos, tileSegment, neighborSegment) =>
      getElement(pos).forall { neighborTile =>
        tile.segments(tileSegment) == neighborTile.segments(neighborSegment)
      }
    }

  /**
   * Retrieves the tile at the specified position, if any.
   *
   * @param position The `Position` to retrieve the tile from.
   * @return An `Option` containing the `GameTile` if one exists at the position, or `None` otherwise.
   */
  def getTile(position: Position): Option[GameTile] =
    getElement(position)

  /**
   * Retrieves the entire map of placed tiles.
   *
   * @return An `Option` containing a `Map` of `Position` to `GameTile`.
   */
  def getTileMap: Option[Map[Position, GameTile]] =
    getElementMap

  def getConnectedFeature(startTile: GameTile, startSegment: TileSegment): Set[(GameTile, TileSegment)] = {
    var visited: Set[(Position, TileSegment)] = Set()

    def oppositeSegment(segment: TileSegment): TileSegment = segment match {
      case TileSegment.N => TileSegment.S
      case TileSegment.S => TileSegment.N
      case TileSegment.E => TileSegment.W
      case TileSegment.W => TileSegment.E
      case other => other
    }

    def getNeighborPosition(position: Position, segment: TileSegment): Position = segment match {
      case TileSegment.N => Position(position.x, position.y - 1)
      case TileSegment.S => Position(position.x, position.y + 1)
      case TileSegment.E => Position(position.x + 1, position.y)
      case TileSegment.W => Position(position.x - 1, position.y)
      case _ => position 
    }

    // Recursive dfs to find connected segments
    def dfs(position: Position, segment: TileSegment): Set[(GameTile, TileSegment)] = {
      if (visited.contains((position, segment))) return Set()

      visited += ((position, segment))

      val currentTileOpt = getTile(position)

      currentTileOpt match {
        case None => Set() 
        case Some(currentTile) =>
          val currentSet = Set((currentTile, segment))
          
          val connectedSegments = currentTile.segments.collect {
            case (seg, segType) if segType == currentTile.segments(segment) && seg != segment => seg
          }

          val sameTileConnections = connectedSegments.flatMap(seg => dfs(position, seg))

          val neighborPosition = getNeighborPosition(position, segment)
          val neighborSegment = oppositeSegment(segment)
          val neighborConnections = dfs(neighborPosition, neighborSegment)

          currentSet ++ sameTileConnections ++ neighborConnections
      }
    }

    val startPositionOpt = getElementMap.flatMap(_.find(_._2 == startTile).map(_._1))

    startPositionOpt match {
      case None => Set() 
      case Some(startPosition) => dfs(startPosition, startSegment)
    }
  }
