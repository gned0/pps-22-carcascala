package carcassonne.model

import play.api.libs.json._

/**
 * Represents the different types of edges that can be found on a tile.
 *
 * `City`: A city segment.
 * `Road`: A road segment.
 * `Field`: A field segment.
 */
enum EdgeType:
  case City, Road, Field


object EdgeType:
  implicit val edgeTypeFormat: Format[EdgeType] = new Format[EdgeType] {
    def reads(json: JsValue): JsResult[EdgeType] = json.as[String] match {
      case "City"  => JsSuccess(EdgeType.City)
      case "Road"  => JsSuccess(EdgeType.Road)
      case "Field" => JsSuccess(EdgeType.Field)
      case other   => JsError(s"Unknown edge type: $other")
    }

    def writes(edgeType: EdgeType): JsValue = JsString(edgeType.toString)
  }
