package carcassonne.util

import carcassonne.model.tile.{GameTile, TileSegment}

object ScalaPrologUtils {

  def stringifyTile: (Position, GameTile) => String = (position: Position, gametile: GameTile) =>
    s"tile(${position.x}, ${position.y}, [${gametile.segments.map((k, v) => 
      s"(${k.toString.toLowerCase}, ${v.toString.toLowerCase})").mkString(", ")}])."

  def stringifyConnectedFeatures: (String, Set[(Position, TileSegment)]) => String = 
    (feature: String, connectedFeatures: Set[(Position, TileSegment)]) =>
    s"${feature}_completed([${connectedFeatures.map((position, segment) => 
      s"(${position.x}, ${position.y}, ${segment.toString.toLowerCase})").mkString(", ")}])"
  
}
