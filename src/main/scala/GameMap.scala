case class Position(x: Int, y: Int)

class GameMap:
  private var tiles: Map[Position, GameTile] = Map()

  def placeTile(tile: GameTile, position: Position): Unit =
    if tiles.contains(position) then
      throw IllegalArgumentException(s"Tile already placed at position $position")
    else
      tiles = tiles + (position -> tile)

  def getTile(position: Position): Option[GameTile] =
    tiles.get(position)
