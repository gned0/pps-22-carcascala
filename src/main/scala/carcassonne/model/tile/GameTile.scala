package carcassonne.model.tile

import carcassonne.model.tile.SegmentType.{City, Field, Road}
import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.TileSegment.*
import play.api.libs.json.*

import scala.util.Random

/** Represents the different segments of a tile in the Carcassonne game.
 * Each tile has nine segments: four edges, four corners, and a center.
 * Each segment corresponds to a specific part of the tile:
 * - N, E, S, W: North, East, South, West edges
 * - NE, SE, SW, NW: Northeast, Southeast, Southwest, Northwest corners
 * - C: Center of the tile
 */
enum TileSegment:
  case N, NE, E, SE, S, SW, W, NW, C

  /** Returns the set of adjacent segments for the current segment.
   *
   * @return A Set of TileSegment that are adjacent to a given segment, according
   *         to the game's official rules.
   */
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

/** Companion object for TileSegment.
 *
 * Contains implicit JSON Format for TileSegment serialization and deserialization.
 */
object TileSegment:

  /** Implicit JSON Format for TileSegment.
   *
   * Provides methods to convert TileSegment to and from JSON.
   */
  implicit val tileSegmentFormat: Format[TileSegment] = new Format[TileSegment] {
    /** Reads a JSON value and converts it to a TileSegment.
     *
     * @param json The JSON value to read from.
     * @return A JsResult containing the TileSegment if successful, or a JsError if not.
     */
    def reads(json: JsValue): JsResult[TileSegment] = json.as[String] match {
      case "N" => JsSuccess(N)
      case "NE" => JsSuccess(NE)
      case "E" => JsSuccess(E)
      case "SE" => JsSuccess(SE)
      case "S" => JsSuccess(S)
      case "SW" => JsSuccess(SW)
      case "W" => JsSuccess(W)
      case "NW" => JsSuccess(NW)
      case "C" => JsSuccess(C)
      case other => JsError(s"Unknown tile segment: $other")
    }

    /** Writes a TileSegment as a JSON value.
     *
     * @param tileSegment The TileSegment to convert to JSON.
     * @return A JsValue representing the TileSegment.
     */
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

  private var followerMap: Map[TileSegment, Int] = Map.empty

  def placeFollower(segment: TileSegment, playerId: Int): Unit =
    followerMap = followerMap.updated(segment, playerId)

  def removeFollower(segment: TileSegment): Unit =
    followerMap = followerMap - segment
  
  def getFollowerMap: Map[TileSegment, Int] = followerMap
  
  /**
   * Rotates the tile 90 degrees clockwise.
   *
   * @return A new GameTile instance with segments rotated clockwise.
   */
  def rotateClockwise: GameTile =
    GameTile(
      rotateSegments(segments, rotateClockwiseMapping),
      imagePath
    )

  /**
   * Rotates the tile 90 degrees counter-clockwise.
   *
   * @return A new GameTile instance with segments rotated counter-clockwise.
   */
  def rotateCounterClockwise: GameTile =
    GameTile(
      rotateSegments(segments, rotateCounterClockwiseMapping),
      imagePath
    )

  private def rotateSegments(matrix: Map[TileSegment, SegmentType], mapping: Map[TileSegment, TileSegment]): Map[TileSegment, SegmentType] =
    matrix.map { case (segment, segmentType) => mapping.getOrElse(segment, segment) -> segmentType }

  private val rotateClockwiseMapping: Map[TileSegment, TileSegment] = Map(
    NW -> NE,
    N -> E,
    NE -> SE,
    W -> N,
    C -> C,
    E -> S,
    SW -> NW,
    S -> W,
    SE -> SW
  )

  private val rotateCounterClockwiseMapping: Map[TileSegment, TileSegment] = Map(
    NW -> SW,
    N -> W,
    NE -> NW,
    W -> S,
    C -> C,
    E -> N,
    SW -> SE,
    S -> E,
    SE -> NE
  )

/** Companion object for GameTile.
 *
 * Provides factory methods for creating GameTile instances and
 * implicit JSON Format for GameTile serialization and deserialization.
 */
object GameTile:

  /** Creates a random GameTile.
   *
   * Generates a tile with random segment types and a random image path.
   *
   * @return A new GameTile instance with randomly assigned segments and image.
   */
  def createRandomTile(): GameTile =
    val segments = TileSegment.values.map { segment =>
      val segmentType = Random.nextInt(3) match {
        case 0 => City
        case 1 => Road
        case 2 => Field
      }
      segment -> segmentType
    }.toMap

    val imagePath = s"RandomTile${Random.nextInt(10)}.png"
    new GameTile(segments, imagePath)

  /** Creates the starting tile for the game.
   *
   * Generates a predefined tile that is by default used to start the game,
   * according to the game's official rules.
   *
   * @return A new GameTile instance representing the starting tile.
   */
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

  /** Implicit JSON Format for GameTile.
   *
   * Provides methods to convert GameTile to and from JSON.
   */
  implicit val gameTileFormat: Format[GameTile] = new Format[GameTile] {
    /** Reads a JSON value and converts it to a GameTile.
     *
     * @param json The JSON value to read from.
     * @return A JsResult containing the GameTile if successful, or a JsError if not.
     */
    def reads(json: JsValue): JsResult[GameTile] = for {
      segments <- (json \ "segments").validate[Map[TileSegment, SegmentType]]
      imagePath <- (json \ "imagePath").validate[String]
    } yield GameTile(segments, imagePath)

    /** Writes a GameTile as a JSON value.
     *
     * @param gameTile The GameTile to convert to JSON.
     * @return A JsValue representing the GameTile.
     */
    def writes(gameTile: GameTile): JsValue = Json.obj(
      "segments" -> gameTile.segments,
      "imagePath" -> gameTile.imagePath
    )
  }
