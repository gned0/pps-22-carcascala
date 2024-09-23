package carcassonne.model.tile

import play.api.libs.json.*

/**
 * Represents the different types of segments that can be found on a tile.
 * Each segment tile has one type, which can be one of the following:
 *  `City`: A city segment.
 *  `Road`: A road segment.
 *  `Field`: A field segment.
 *  `Monastery`: A monastery segment.
 *  `RoadEnd`: A road end segment.
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
