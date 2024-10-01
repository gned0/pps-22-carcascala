package carcassonne.model.tile

import play.api.libs.json.*
import scala.util.{Try, Success, Failure}

/** Represents the different types of segments that can be found on a tile. Each segment tile has one type, which can be one of the following:
  *   - `City`: A city segment.
  *   - `Road`: A road segment.
  *   - `Field`: A field segment.
  *   - `Monastery`: A monastery segment.
  *   - `RoadEnd`: A road end segment.
  */
enum SegmentType derives CanEqual:
  /** A city segment.
    */
  case City

  /** A road segment.
    */
  case Road

  /** A field segment.
    */
  case Field

  /** A monastery segment.
    */
  case Monastery

  /** A road end segment.
    */
  case RoadEnd

object SegmentType:
  /** Implicit JSON format for `SegmentType`. Provides methods to convert `SegmentType` to and from JSON.
    */
  given Format[SegmentType] with
    /** Reads a `SegmentType` from JSON.
      *
      * @param json
      *   The JSON value to read from.
      * @return
      *   A `JsResult` containing the `SegmentType` if successful, or an error message if not.
      */
    def reads(json: JsValue): JsResult[SegmentType] =
      json.as[String] match
        case "City"      => JsSuccess(SegmentType.City)
        case "Road"      => JsSuccess(SegmentType.Road)
        case "Field"     => JsSuccess(SegmentType.Field)
        case "Monastery" => JsSuccess(SegmentType.Monastery)
        case "RoadEnd"   => JsSuccess(SegmentType.RoadEnd)
        case other       => JsError(s"Unknown segment type: $other")

    /** Writes a `SegmentType` to JSON.
      *
      * @param segmentType
      *   The `SegmentType` to write.
      * @return
      *   A `JsValue` representing the `SegmentType`.
      */
    def writes(segmentType: SegmentType): JsValue = JsString(segmentType.toString)

  /** Attempts to parse a `SegmentType` from a string.
    *
    * @param str
    *   The string to parse.
    * @return
    *   A `Try` containing the `SegmentType` if successful, or an exception if not.
    */
  def fromString(str: String): Try[SegmentType] =
    str match
      case "City"      => Success(SegmentType.City)
      case "Road"      => Success(SegmentType.Road)
      case "Field"     => Success(SegmentType.Field)
      case "Monastery" => Success(SegmentType.Monastery)
      case "RoadEnd"   => Success(SegmentType.RoadEnd)
      case other       => Failure(new IllegalArgumentException(s"Unknown segment type: $other"))
