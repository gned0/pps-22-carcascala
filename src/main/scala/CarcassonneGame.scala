class CarcassonneGame {
  private var board = Map[(Int, Int), String]()

  def getBoardSize: Int = board.size

  def placeTile(x: Int, y: Int, tileType: String): Unit = {
    if (board.contains((x, y))) {
      throw new IllegalArgumentException("Position is already occupied")
    }
    board += (x, y) -> tileType
  }

  def getTileAt(x: Int, y: Int): Option[String] = board.get((x, y))
}
