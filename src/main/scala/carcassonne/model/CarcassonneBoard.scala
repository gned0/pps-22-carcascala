package carcassonne.model

import carcassonne.util.Logger

/**
 * Represents the game map, which holds the placed tiles.
 *
 * This class extends `GameBoard[GameTile]`, meaning it can manage a board of tiles.
 */
class CarcassonneBoard extends GameBoard[GameTile]:

  /**
   * Places a tile on the map at the specified position.
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
      true
    else
      throw IllegalArgumentException(s"Invalid tile placement at position $position")

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

    // Check that for each neighbor, if it exists, the segments match appropriately
    neighbors.forall { case (pos, tileSegment, neighborSegment) =>
      getElement(pos).forall { neighborTile =>
        // Compare the segment types of the current tile and the neighbor tile
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