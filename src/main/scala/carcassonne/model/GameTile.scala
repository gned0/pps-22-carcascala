package carcassonne.model

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue, Json}

enum TileSegment:
  case N, NE, E, SE, S, SW, W, NW, C

object TileSegment:
  implicit val tileSegmentFormat: Format[TileSegment] = new Format[TileSegment] {
    def reads(json: JsValue): JsResult[TileSegment] = json.as[String] match {
      case "N"  => JsSuccess(TileSegment.N)
      case "NE" => JsSuccess(TileSegment.NE)
      case "E"  => JsSuccess(TileSegment.E)
      case "SE" => JsSuccess(TileSegment.SE)
      case "S"  => JsSuccess(TileSegment.S)
      case "SW" => JsSuccess(TileSegment.SW)
      case "W"  => JsSuccess(TileSegment.W)
      case "NW" => JsSuccess(TileSegment.NW)
      case "C"  => JsSuccess(TileSegment.C)
      case other => JsError(s"Unknown tile segment: $other")
    }

    def writes(tileSegment: TileSegment): JsValue = JsString(tileSegment.toString)
  }

/**
 * Represents a tile in the game.
 *
 * @param segments A map representing all the segments of the tile and their types.
 * @param imagePath The path to the image representing this tile.
 */
case class GameTile(
                     segments: Map[TileSegment, SegmentType],
                     imagePath: String
                   ):

  // Initialize followerMap as an empty map by default
  var followerMap: Map[TileSegment, Int] = Map.empty

  /**
   * Rotates the tile 90 degrees clockwise.
   *
   * @return A new GameTile instance with segments rotated clockwise.
   */
  def rotateClockwise: GameTile =
    GameTile(
      rotateSegmentsClockwise(segments),
      imagePath
    )

  def rotateCounterClockwise: GameTile =
    GameTile(
      rotateSegmentsCounterClockwise(segments),
      imagePath
    )

  private def rotateSegmentsClockwise(matrix: Map[TileSegment, SegmentType]): Map[TileSegment, SegmentType] =
    Map(
      TileSegment.NW -> matrix.getOrElse(TileSegment.SW, SegmentType.Field),
      TileSegment.N  -> matrix.getOrElse(TileSegment.W, SegmentType.Field),
      TileSegment.NE -> matrix.getOrElse(TileSegment.NW, SegmentType.Field),
      TileSegment.W  -> matrix.getOrElse(TileSegment.S, SegmentType.Field),
      TileSegment.C  -> matrix.getOrElse(TileSegment.C, SegmentType.Field),
      TileSegment.E  -> matrix.getOrElse(TileSegment.N, SegmentType.Field),
      TileSegment.SW -> matrix.getOrElse(TileSegment.SE, SegmentType.Field),
      TileSegment.S  -> matrix.getOrElse(TileSegment.E, SegmentType.Field),
      TileSegment.SE -> matrix.getOrElse(TileSegment.NE, SegmentType.Field)
    )

  private def rotateSegmentsCounterClockwise(matrix: Map[TileSegment, SegmentType]): Map[TileSegment, SegmentType] =
    Map(
      TileSegment.NW -> matrix.getOrElse(TileSegment.NE, SegmentType.Field),
      TileSegment.N  -> matrix.getOrElse(TileSegment.E, SegmentType.Field),
      TileSegment.NE -> matrix.getOrElse(TileSegment.SE, SegmentType.Field),
      TileSegment.W  -> matrix.getOrElse(TileSegment.N, SegmentType.Field),
      TileSegment.C  -> matrix.getOrElse(TileSegment.C, SegmentType.Field),
      TileSegment.E  -> matrix.getOrElse(TileSegment.S, SegmentType.Field),
      TileSegment.SW -> matrix.getOrElse(TileSegment.NW, SegmentType.Field),
      TileSegment.S  -> matrix.getOrElse(TileSegment.W, SegmentType.Field),
      TileSegment.SE -> matrix.getOrElse(TileSegment.SW, SegmentType.Field)
    )


object GameTile:

  implicit val gameTileFormat: Format[GameTile] = new Format[GameTile] {
    def reads(json: JsValue): JsResult[GameTile] = for {
      segments <- (json \ "segments").validate[Map[TileSegment, SegmentType]]
      imagePath <- (json \ "imagePath").validate[String]
    } yield GameTile(segments, imagePath)

    def writes(gameTile: GameTile): JsValue = Json.obj(
      "segments" -> gameTile.segments,
      "imagePath" -> gameTile.imagePath
    )
  }

  /**
   * The starting tile of the game, with specific segment types as per the game rules:
   *
   * - **North (N)**: City
   * - **East (E)**: Road
   * - **South (S)**: Field
   * - **West (W)**: Road
   * - **North-West (NW)**: Field
   * - **North-East (NE)**: Field
   * - **South-West (SW)**: Field
   * - **South-East (SE)**: Field
   * - **Center (C)**: Road
   */
  val startTile: GameTile = GameTile(
    Map(
      TileSegment.NW -> SegmentType.Field,
      TileSegment.N  -> SegmentType.City,
      TileSegment.NE -> SegmentType.Field,
      TileSegment.W  -> SegmentType.Road,
      TileSegment.C  -> SegmentType.Road,
      TileSegment.E  -> SegmentType.Road,
      TileSegment.SW -> SegmentType.Field,
      TileSegment.S  -> SegmentType.Field,
      TileSegment.SE -> SegmentType.Field
    ),
    "CastleSideRoad.png"
  )
