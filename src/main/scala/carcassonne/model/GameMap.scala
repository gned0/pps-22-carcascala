package carcassonne.model

import carcassonne.observers.SubjectGameMap
import carcassonne.util.Logger
/**
 * Represents a position on the game map.
 *
 * @param x The x-coordinate of the position.
 * @param y The y-coordinate of the position.
 */
case class Position(x: Int, y: Int)

/**
 * Represents the game map, which holds the placed tiles.
 *
 * This class extends `observers.Subject[GameMap]`, meaning it can notify observers of changes.
 */
class GameMap extends SubjectGameMap[GameMap]:
  private var tiles: Map[Position, GameTile] = Map()

  /**
   * Places a tile on the map at the specified position.
   *
   * @param tile The `GameTile` to place.
   * @param position The `Position` where the tile should be placed.
   * @throws IllegalArgumentException if a tile is already placed at the position or if the placement is invalid.
   */
  def placeTile(tile: GameTile, position: Position): Unit =
    if tiles.contains(position) then
      notifyIsTilePlaced(false, this.getTileMap, position)
      throw IllegalArgumentException(s"Tile already placed at position $position")

    if isValidPlacement(tile, position) then
      tiles = tiles + (position -> tile)
      notifyIsTilePlaced(true, this.getTileMap, position)
      Logger.log(s"MODEL", s"Tile placed")
    else
      notifyIsTilePlaced(false, this.getTileMap, position)
      throw IllegalArgumentException(s"Invalid tile placement at position $position")

  /**
   * Retrieves the tile at the specified position, if any.
   *
   * @param position The `Position` to retrieve the tile from.
   * @return An `Option` containing the `GameTile` if one exists at the position, or `None` otherwise.
   */
  def getTile(position: Position): Option[GameTile] =
    tiles.get(position)

  /**
   * Retrieves the entire map of placed tiles.
   *
   * @return An `Option` containing a `Map` of `Position` to `GameTile`.
   */
  def getTileMap: Option[Map[Position, GameTile]] = Option.apply(this.tiles)

  /**
   * Validates whether a tile can be placed at the specified position.
   *
   * @param tile The `GameTile` to validate.
   * @param position The `Position` where the tile is to be placed.
   * @return `true` if the placement is valid, `false` otherwise.
   */
  private def isValidPlacement(tile: GameTile, position: Position): Boolean =
    val neighbors = List(
      (Position(position.x, position.y - 1), tile.north, (gt: GameTile) => gt.south), // North neighbor
      (Position(position.x + 1, position.y), tile.east, (gt: GameTile) => gt.west), // East neighbor
      (Position(position.x, position.y + 1), tile.south, (gt: GameTile) => gt.north), // South neighbor
      (Position(position.x - 1, position.y), tile.west, (gt: GameTile) => gt.east) // West neighbor
    )

    neighbors.forall { case (pos, tileEdge, getNeighborEdge) =>
      tiles.get(pos).forall(neighborTile => getNeighborEdge(neighborTile) == tileEdge)
    }
