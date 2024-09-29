package carcassonne.model.tile

import carcassonne.model.tile.SegmentType.{City, Field, Road}
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
  given Format[TileSegment] with
    def reads(json: JsValue): JsResult[TileSegment] = json.as[String] match
      case "N" => JsSuccess(TileSegment.N)
      case "NE" => JsSuccess(TileSegment.NE)
      case "E" => JsSuccess(TileSegment.E)
      case "SE" => JsSuccess(TileSegment.SE)
      case "S" => JsSuccess(TileSegment.S)
      case "SW" => JsSuccess(TileSegment.SW)
      case "W" => JsSuccess(TileSegment.W)
      case "NW" => JsSuccess(TileSegment.NW)
      case "C" => JsSuccess(TileSegment.C)
      case other => JsError(s"Unknown tile segment: $other")

    def writes(tileSegment: TileSegment): JsValue = JsString(tileSegment.toString)

/** Represents a tile in the game.
 *
 * @param segments A map representing all the segments of the tile and their types.
 * @param imagePath The path to the image representing this tile.
 */
case class GameTile(segments: Map[TileSegment, SegmentType], imagePath: String):

  private var followerMap: Map[TileSegment, Int] = Map.empty

  /** Places a follower on a specific segment of the tile.
   *
   * @param segment The segment where the follower is to be placed.
   * @param playerId The ID of the player placing the follower.
   */
  def placeFollower(segment: TileSegment, playerId: Int): Unit =
    followerMap = followerMap.updated(segment, playerId)

  /** Removes a follower from a specific segment of the tile.
   *
   * @param segment The segment from which the follower is to be removed.
   */
  def removeFollower(segment: TileSegment): Unit =
    followerMap = followerMap.removed(segment)

  /** Retrieves the current map of followers on the tile.
   *
   * @return A map where keys are segments and values are player IDs.
   */
  def getFollowerMap: Map[TileSegment, Int] = followerMap

  /** Rotates the tile 90 degrees clockwise.
   *
   * @return A new GameTile instance with segments rotated clockwise.
   */
  def rotateClockwise: GameTile =
    GameTile(segments.map { case (segment, segmentType) => rotateClockwiseMapping(segment) -> segmentType }, imagePath)

  /** Rotates the tile 90 degrees counter-clockwise.
   *
   * @return A new GameTile instance with segments rotated counter-clockwise.
   */
  def rotateCounterClockwise: GameTile =
    GameTile(segments.map { case (segment, segmentType) => rotateCounterClockwiseMapping(segment) -> segmentType }, imagePath)

  private val rotateClockwiseMapping: PartialFunction[TileSegment, TileSegment] = {
    case TileSegment.NW => TileSegment.NE
    case TileSegment.N => TileSegment.E
    case TileSegment.NE => TileSegment.SE
    case TileSegment.W => TileSegment.N
    case TileSegment.C => TileSegment.C
    case TileSegment.E => TileSegment.S
    case TileSegment.SW => TileSegment.NW
    case TileSegment.S => TileSegment.W
    case TileSegment.SE => TileSegment.SW
  }

  private val rotateCounterClockwiseMapping: PartialFunction[TileSegment, TileSegment] = {
    case TileSegment.NW => TileSegment.SW
    case TileSegment.N => TileSegment.W
    case TileSegment.NE => TileSegment.NW
    case TileSegment.W => TileSegment.S
    case TileSegment.C => TileSegment.C
    case TileSegment.E => TileSegment.N
    case TileSegment.SW => TileSegment.SE
    case TileSegment.S => TileSegment.E
    case TileSegment.SE => TileSegment.NE
  }

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
      val segmentType = Random.nextInt(3) match
        case 0 => City
        case 1 => Road
        case 2 => Field
      segment -> segmentType
    }.toMap

    val imagePath = s"RandomTile${Random.nextInt(10)}.png"
    GameTile(segments, imagePath)

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
        TileSegment.NW -> Field,
        TileSegment.N -> City,
        TileSegment.NE -> Field,
        TileSegment.W -> Road,
        TileSegment.C -> Road,
        TileSegment.E -> Road,
        TileSegment.SW -> Field,
        TileSegment.S -> Field,
        TileSegment.SE -> Field
      ),
      "CastleSideRoad.png"
    )

  /** Implicit JSON Format for GameTile.
   *
   * Provides methods to convert GameTile to and from JSON.
   */
  given Format[GameTile] with
    def reads(json: JsValue): JsResult[GameTile] = for
      segments <- (json \ "segments").validate[Map[TileSegment, SegmentType]]
      imagePath <- (json \ "imagePath").validate[String]
    yield GameTile(segments, imagePath)

    def writes(gameTile: GameTile): JsValue = Json.obj(
      "segments" -> gameTile.segments,
      "imagePath" -> gameTile.imagePath
    )