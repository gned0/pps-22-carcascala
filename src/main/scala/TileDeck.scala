import scala.util.Random

class TileDeck {
  private var tiles: List[GameTile] = List()

  def isEmpty: Boolean = tiles.isEmpty

  private def shuffle(): Unit = {
    tiles = Random.shuffle(tiles)
  }

  def initializeDeck(): Unit = {
    // TODO: Implement the method to initialize the deck
  }
}