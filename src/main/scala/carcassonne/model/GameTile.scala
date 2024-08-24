package carcassonne.model

import play.api.libs.json.{Format, JsResult, JsValue, Json}

/**
 * Represents a tile in the game.
 *
 * @param north The edge type on the northern side of the tile.
 * @param east The edge type on the eastern side of the tile.
 * @param south The edge type on the southern side of the tile.
 * @param west The edge type on the western side of the tile.
 */
case class GameTile(north: EdgeType,
                    east: EdgeType,
                    south: EdgeType,
                    west: EdgeType,
                    meeplePositions: Map[String, String],
                    imgPath: String):

  /**
   * Rotates the tile 90 degrees clockwise.
   *
   * @return A new `GameTile` instance with edges rotated clockwise.
   */
  def rotate: GameTile = GameTile(west, north, east, south,  meeplePositions, imgPath)

object GameTile:
  implicit val gameTileFormat: Format[GameTile] = new Format[GameTile] {
    def reads(json: JsValue): JsResult[GameTile] = for {
      north <- (json \ "north").validate[EdgeType]
      east <- (json \ "east").validate[EdgeType]
      south <- (json \ "south").validate[EdgeType]
      west <- (json \ "west").validate[EdgeType]
      meeplePositions <- (json \ "meeplePositions").validate[Map[String, String]]
      imgPath <- (json \ "imagePath").validate[String]
    } yield GameTile(north, east, south, west, meeplePositions, imgPath)

    def writes(gameTile: GameTile): JsValue = Json.obj(
      "north" -> gameTile.north,
      "east" -> gameTile.east,
      "south" -> gameTile.south,
      "west" -> gameTile.west,
      "meeplePositions" -> gameTile.meeplePositions,
      "imagePath" -> gameTile.imgPath
    )
  }
  /**
   * The starting tile of the game, with specific edges as per the game rules:
   * - North: `City`
   * - East: `Road`
   * - South: `Field`
   * - West: `Road`
   */
  val startTile: GameTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.Road, Map(
    "NW" -> "Field", "N"-> "City", "NE"-> "Field",
    "W"-> "Road", "C"-> "Road", "E"-> "Road",
    "SW"-> "Field", "S"-> "Field", "SE"-> "Field"
  ), imgPath = "CastleSideRoad.png")
