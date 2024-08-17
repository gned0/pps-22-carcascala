case class Position(x: Int, y: Int)

class GameMap extends Subject[GameMap]:
  private var tiles: Map[Position, GameTile] = Map()

  def placeTile(tile: GameTile, position: Position): Unit =
    if tiles.contains(position) then
      throw IllegalArgumentException(s"Tile already placed at position $position")
    else
      tiles = tiles + (position -> tile)
      log("Tile placed")
      notifyObservers()

  def getTile(position: Position): Option[GameTile] =
    tiles.get(position)

  def getTileMap(): Option[Map[Position, GameTile]] = Option.apply(this.tiles)

  def log(string: String): Unit =
    print(s"MODEL - " + string + "\n")
