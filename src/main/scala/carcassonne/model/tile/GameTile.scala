package carcassonne.model.tile

import carcassonne.model.tile.SegmentType.{City, Field, Road}
import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.TileSegment.*
import play.api.libs.json.*

import scala.util.Random

enum TileSegment:
  case N, NE, E, SE, S, SW, W, NW, C

  def adjacentSegments: Set[TileSegment] = this match
    case N => Set(NW, NE, C)
    case NE => Set(N, E)
    case E => Set(NE, SE, C)
    case SE => Set(E, S)
    case S => Set(SE, SW, C)
    case SW => Set(S, W)
    case W => Set(SW, NW, C)
    case NW => Set(W, N)
    case C => Set(N, E, S, W)

object TileSegment:

  implicit val tileSegmentFormat: Format[TileSegment] = new Format[TileSegment] {
    def reads(json: JsValue): JsResult[TileSegment] = json.as[String] match {
      case "N"  => JsSuccess(N)
      case "NE" => JsSuccess(NE)
      case "E"  => JsSuccess(E)
      case "SE" => JsSuccess(SE)
      case "S"  => JsSuccess(S)
      case "SW" => JsSuccess(SW)
      case "W"  => JsSuccess(W)
      case "NW" => JsSuccess(NW)
      case "C"  => JsSuccess(C)
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

  var followerMap: Map[TileSegment, Int] = Map.empty

  /**
   * Rotates the tile 90 degrees clockwise.
   *
   * @return A new GameTile instance with segments rotated clockwise.
   */
//  def rotateClockwise: GameTile = copy(segments = rotate(segments, clockwise = true))
//
//  def rotateCounterClockwise: GameTile = copy(segments = rotate(segments, clockwise = false))
//
//  private def rotate(segments: Map[TileSegment, SegmentType], clockwise: Boolean): Map[TileSegment, SegmentType] =
//    val rotationMap = if clockwise then Map(
//      NW -> SW, N -> W, NE -> NW, W -> S, C -> C, E -> N, SW -> SE, S -> E, SE -> NE
//    ) else Map(
//      NW -> NE, N -> E, NE -> SE, W -> N, C -> C, E -> S, SW -> NW, S -> W, SE -> SW
//    )
//    segments.map { case (k, v) => rotationMap(k) -> v }

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
      TileSegment.N -> matrix.getOrElse(TileSegment.W, SegmentType.Field),
      TileSegment.NE -> matrix.getOrElse(TileSegment.NW, SegmentType.Field),
      TileSegment.W -> matrix.getOrElse(TileSegment.S, SegmentType.Field),
      TileSegment.C -> matrix.getOrElse(TileSegment.C, SegmentType.Field),
      TileSegment.E -> matrix.getOrElse(TileSegment.N, SegmentType.Field),
      TileSegment.SW -> matrix.getOrElse(TileSegment.SE, SegmentType.Field),
      TileSegment.S -> matrix.getOrElse(TileSegment.E, SegmentType.Field),
      TileSegment.SE -> matrix.getOrElse(TileSegment.NE, SegmentType.Field)
    )

  private def rotateSegmentsCounterClockwise(matrix: Map[TileSegment, SegmentType]): Map[TileSegment, SegmentType] =
    Map(
      TileSegment.NW -> matrix.getOrElse(TileSegment.NE, SegmentType.Field),
      TileSegment.N -> matrix.getOrElse(TileSegment.E, SegmentType.Field),
      TileSegment.NE -> matrix.getOrElse(TileSegment.SE, SegmentType.Field),
      TileSegment.W -> matrix.getOrElse(TileSegment.N, SegmentType.Field),
      TileSegment.C -> matrix.getOrElse(TileSegment.C, SegmentType.Field),
      TileSegment.E -> matrix.getOrElse(TileSegment.S, SegmentType.Field),
      TileSegment.SW -> matrix.getOrElse(TileSegment.NW, SegmentType.Field),
      TileSegment.S -> matrix.getOrElse(TileSegment.W, SegmentType.Field),
      TileSegment.SE -> matrix.getOrElse(TileSegment.SW, SegmentType.Field)
    )

object GameTile:

  def createRandomTile(): GameTile = 
    val segments = TileSegment.values.map { segment =>
      val segmentType = Random.nextInt(3) match {
        case 0 => SegmentType.City
        case 1 => SegmentType.Road
        case 2 => SegmentType.Field
      }
      segment -> segmentType
    }.toMap

    val imagePath = s"RandomTile${Random.nextInt(10)}.png"
    new GameTile(segments, imagePath)

  def createStartTile(): GameTile = 
    GameTile(
      Map(
        NW -> Field,
        N -> City,
        NE -> Field,
        W -> Road,
        C -> Road,
        E -> Road,
        SW -> Field,
        S -> Field,
        SE -> Field
      ),
      "CastleSideRoad.png"
    )

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

