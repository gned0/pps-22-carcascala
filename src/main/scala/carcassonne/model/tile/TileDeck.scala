package carcassonne.model.tile

import carcassonne.model.tile.GameTile
import carcassonne.util.Logger
import play.api.libs.json.*

import scala.io.Source
import scala.util.Random
import scala.collection.immutable.LazyList

trait TileDeck:
  def draw(): Option[GameTile]

object TileDeck:
  def apply(configFilePath: String = "src/main/resources/deck.json"): TileDeck =
    new TileDeckImpl(loadTiles(configFilePath))

  private def loadTiles(configFilePath: String): LazyList[GameTile] =
    val source = Source.fromFile(configFilePath)
    try
      LazyList.from(Json.parse(source.mkString).validate[List[GameTile]].getOrElse(List.empty))
    finally
      source.close()

  private class TileDeckImpl(initialTiles: LazyList[GameTile]) extends TileDeck:
    private var tiles: LazyList[GameTile] = Random.shuffle(initialTiles)

    override def draw(): Option[GameTile] =
      tiles.headOption.map { head =>
        tiles = tiles.tail
        Logger.log("TILEDECK", s"Card Drawn: $head")
        head
      }