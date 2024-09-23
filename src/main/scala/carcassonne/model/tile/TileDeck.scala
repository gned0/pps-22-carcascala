package carcassonne.model.tile

import carcassonne.model.tile.GameTile
import carcassonne.util.Logger
import play.api.libs.json.*

import scala.io.Source
import scala.util.Random
import scala.collection.immutable.LazyList

/** Represents a deck of tiles in the Carcassonne game.
 *
 *  This trait defines the basic functionality of a tile deck,
 *  which is to draw tiles. Note that because the only condition that
 *  ends a game is the deck being empty, the draw functionality is also used to
 *  handle the end of the game.
 */
trait TileDeck:
  /** Draws a tile from the deck.
   *
   *  @return An Option containing the drawn GameTile, or None if the deck is empty.
   */
  def draw(): Option[GameTile]

/** Companion object for TileDeck.
 *
 *  This object provides factory methods to create TileDeck instances
 *  and handles the loading of tiles from a configuration file.
 */
object TileDeck:
  
  private val defaultConfigFilePath: String = "src/main/resources/deck.json"

  /** Creates a new TileDeck instance.
   *
   * @param configFilePath The path to the JSON configuration file for the deck.
   *                       Defaults to "src/main/resources/deck.json", which
   *                       is the standard deck configuration for the game as
   *                       specified in the official rules.
   * @return A new TileDeck instance.
   */
  def apply(configFilePath: String = defaultConfigFilePath): TileDeck =
    new TileDeckImpl(loadTiles(configFilePath))

  /** Loads tiles from a JSON configuration file.
   *
   *  @param configFilePath The path to the JSON configuration file.
   *  @return A LazyList of GameTile objects.
   */
  private def loadTiles(configFilePath: String): LazyList[GameTile] =
    val source = Source.fromFile(configFilePath)
    try
      LazyList.from(Json.parse(source.mkString).validate[List[GameTile]].getOrElse(List.empty))
    finally
      source.close()

  /** Private implementation of the TileDeck trait.
   *
   *  @param initialTiles The initial set of tiles to populate the deck.
   */
  private class TileDeckImpl(initialTiles: LazyList[GameTile]) extends TileDeck:
    
    // deck is shuffled to randomize the order of tiles
    private var tiles: LazyList[GameTile] = Random.shuffle(initialTiles)
    
    override def draw(): Option[GameTile] =
      tiles.headOption match
        case Some(head) =>
          tiles = tiles.tail
          Logger.log("TILEDECK", s"Card Drawn: $head")
          Some(head)
        case None =>
          Logger.log("TILEDECK", "No more tiles to draw.")
          None