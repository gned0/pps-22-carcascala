import play.api.libs.json.{Json, OFormat}

import scala.io.Source
import scala.util.Random

private case class TileConfig(north: String, east: String, south: String, west: String)

object TileConfig {
  implicit val format: OFormat[TileConfig] = Json.format[TileConfig]
}
/**
 * Represents a deck of game tiles.
 *
 */
class TileDeck {

  private var tiles: List[GameTile] = List()

  // Path to the default JSON configuration file
  private val defaultConfigFilePath: String = "src/main/resources/deck.json"

  // Initialize the deck using the default config file and shuffle it
  initializeDeck(defaultConfigFilePath)
  shuffle()

  /**
   * Checks if the deck is empty.
   *
   * @return `true` if there are no tiles left in the deck, `false` otherwise.
   */
  def isEmpty: Boolean = tiles.isEmpty

  /**
   * Shuffles the tiles in the deck (randomly rearranges the order of the tiles in the deck).
   *
   */
  private def shuffle(): Unit = {
    tiles = Random.shuffle(tiles)
  }

  /**
   * Draws the top tile from the deck.
   *
   * This method removes the first tile from the list of tiles and returns it.
   * If the deck is empty, it returns `None`.
   *
   * @return An `Option[GameTile]` containing the drawn tile, or `None` if the deck is empty.
   */
  def draw(): Option[GameTile] = {
    tiles match {
      case Nil => None
      case head :: tail =>
        tiles = tail
        Some(head)
    }
  }

  /**
   * Initializes the deck by loading tile configurations from a JSON file.
   *
   * @param configFilePath The path to the JSON configuration file.
   */
  private def initializeDeck(configFilePath: String): Unit = {
    // Load the JSON file
    val source = Source.fromFile(configFilePath)
    val jsonString = try source.mkString finally source.close()

    // Parse the JSON into a list of TileConfig
    val tileConfigs = Json.parse(jsonString).as[List[TileConfig]]
    
    // Convert TileConfig to GameTile and populate the deck
    tiles = tileConfigs.map { config =>
      GameTile(
        EdgeType.valueOf(config.north),
        EdgeType.valueOf(config.east),
        EdgeType.valueOf(config.south),
        EdgeType.valueOf(config.west)
      )
    }
  }

  /**
   * Returns the number of tiles currently in the deck.
   *
   * @return An `Int` representing the number of tiles in the deck.
   */
  def getSize: Int = tiles.size
}