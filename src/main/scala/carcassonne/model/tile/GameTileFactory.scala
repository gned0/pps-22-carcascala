package carcassonne.model.tile

import scala.util.Random

object GameTileFactory {

  def createRandomTile(): GameTile = {
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
  }
  
  def createStartTile(): GameTile = {
    GameTile(
      Map(
        TileSegment.NW -> SegmentType.Field,
        TileSegment.N -> SegmentType.City,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.C -> SegmentType.Road,
        TileSegment.E -> SegmentType.Road,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field
      ),
      "CastleSideRoad.png"
    )
  }
}
