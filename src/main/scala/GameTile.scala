enum EdgeType:
  case City, Road, Field

case class GameTile(north: EdgeType, east: EdgeType, south: EdgeType, west: EdgeType):
  def rotate: GameTile = GameTile(west, north, east, south)

object GameTile:
  val startTile: GameTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.Road)