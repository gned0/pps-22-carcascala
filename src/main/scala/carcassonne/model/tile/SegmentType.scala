package carcassonne.model.tile

import carcassonne.model.tile.SegmentType
import play.api.libs.json.*

/**
 * Represents the different types of edges that can be found on a tile.
 *
 * `City`: A city segment.
 * `Road`: A road segment.
 * `Field`: A field segment.
 */
enum SegmentType:
  case City, Road, Field, Monastery, RoadEnd


object SegmentType:
  implicit val segmentTypeFormat: Format[SegmentType] = new Format[SegmentType] {
    def reads(json: JsValue): JsResult[SegmentType] = json.as[String] match {
      case "City"  => JsSuccess(SegmentType.City)
      case "Road"  => JsSuccess(SegmentType.Road)
      case "Field" => JsSuccess(SegmentType.Field)
      case "Monastery" => JsSuccess(SegmentType.Monastery)
      case "RoadEnd" => JsSuccess(SegmentType.RoadEnd)
      case other   => JsError(s"Unknown segment type: $other")
    }

    def writes(segmentType: SegmentType): JsValue = JsString(segmentType.toString)
  }
