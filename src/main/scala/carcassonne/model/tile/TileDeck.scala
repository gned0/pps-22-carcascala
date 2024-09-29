package carcassonne.model.tile

import carcassonne.util.Logger
import play.api.libs.json.*
import scala.io.Source
import scala.util.{Random, Using}
import scala.collection.immutable.LazyList

/** Represents a deck of tiles in the Carcassonne game.
 *
 * This trait defines the basic functionality of a tile deck, which is to draw tiles. Note that because the only
 * condition that ends a game is the deck being empty, the draw functionality is also used to handle the end of the
 * game.
 */
trait TileDeck:
  /** Draws a tile from the deck.
   *
   * @return
   *   An Option containing the drawn GameTile, or None if the deck is empty.
   */
  def draw(): Option[GameTile]

  /** Get the number of remaining cards
   *
   * @return
   *   An Int with the tile count.
   */
  def getTileCount: Int

/** Companion object for TileDeck.
 *
 * This object provides factory methods to create TileDeck instances and handles the loading of tiles from a
 * configuration file.
 */
object TileDeck:

  private val defaultConfigFilePath: String = "/deck.json"

  /** Creates a new TileDeck instance.
   *
   * @param configFilePath
   *   The path to the JSON configuration file for the deck. Defaults to "src/main/resources/deck.json", which is the
   *   standard deck configuration for the game as specified in the official rules.
   * @return
   *   A new TileDeck instance.
   */
  def apply(configFilePath: String = defaultConfigFilePath): TileDeck =
    new TileDeckImpl(loadTileCount(configFilePath), loadTiles(configFilePath))

  /** Loads tilesCount from a JSON configuration file.
   *
   * @param configFilePath
   *   The path to the JSON configuration file.
   * @return
   *   An Int representing the number of game tiles.
   */
  private def loadTileCount(configFilePath: String): Int =
    Using.resource(Source.fromInputStream(getClass.getResourceAsStream(configFilePath))) { source =>
      val jsonString = source.getLines().mkString
      (Json.parse(jsonString) \ "tileCount").as[Int]
    }

  /** Loads tiles from a JSON configuration file.
   *
   * @param configFilePath
   *   The path to the JSON configuration file.
   * @return
   *   A LazyList of GameTile objects.
   */
  private def loadTiles(configFilePath: String): LazyList[GameTile] =
    Using.resource(Source.fromInputStream(getClass.getResourceAsStream(configFilePath))) { source =>
      val jsonString = source.getLines().mkString
      LazyList.from((Json.parse(jsonString) \ "tiles").validate[List[GameTile]].getOrElse(List.empty))
    }

  /** Private implementation of the TileDeck trait.
   *
   * @param initialTiles
   *   The initial set of tiles to populate the deck.
   */
  private class TileDeckImpl(count: Int, initialTiles: LazyList[GameTile]) extends TileDeck:

    // deck is shuffled to randomize the order of tiles
    private var tiles: LazyList[GameTile] = Random.shuffle(initialTiles)
    private var tileCount = count

    /** Draws a tile from the deck.
     *
     * @return
     *   An Option containing the drawn GameTile, or None if the deck is empty.
     */
    override def draw(): Option[GameTile] =
      tiles.headOption.map { head =>
        tiles = tiles.tail
        tileCount -= 1
        Logger.log("TILEDECK", s"Card Drawn: $head")
        head
      }.orElse {
        Logger.log("TILEDECK", "No more tiles to draw.")
        None
      }

    /** Get the number of remaining cards
     *
     * @return
     *   An Int with the tile count.
     */
    override def getTileCount: Int = tileCount