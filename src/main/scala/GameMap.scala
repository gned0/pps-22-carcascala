case class Position(x: Int, y: Int)

class GameMap extends Subject[GameMap]:
  private var tiles: Map[Position, GameTile] = Map()

  def placeTile(tile: GameTile, position: Position): Unit =
    if tiles.contains(position) then
      throw IllegalArgumentException(s"Tile already placed at position $position")

    if isValidPlacement(tile, position) then
      tiles = tiles + (position -> tile)
      log("Tile placed")
      notifyObservers()
    else
      throw IllegalArgumentException(s"Invalid tile placement at position $position")

  def getTile(position: Position): Option[GameTile] =
    tiles.get(position)

  def getTileMap: Option[Map[Position, GameTile]] = Option.apply(this.tiles)

  def log(string: String): Unit =
    print(s"MODEL - " + string + "\n")

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