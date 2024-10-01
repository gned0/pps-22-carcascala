package carcassonne.util

import carcassonne.model.tile.{GameTile, TileSegment}

/** Utility object providing methods to stringify game-related data in a format compatible with Prolog. It helps convert game elements like tiles and connected features
  * into Prolog-readable strings.
  */
object ScalaPrologUtils {

  /** Converts a `GameTile` and its `Position` into a Prolog-readable string representation.
    *
    * The resulting string will be in the format: `tile(x, y, [(segment, type), ...])`
    *
    * @return
    *   A function that takes a `Position` and a `GameTile` and returns a formatted string.
    */
  def stringifyTile: (Position, GameTile) => String = (position: Position, gametile: GameTile) =>
    s"tile(${position.x}, ${position.y}, [${gametile.segments.map((k, v) => s"(${k.toString.toLowerCase}, ${v.toString.toLowerCase})").mkString(", ")}])."

  /** Converts a set of connected features into a Prolog-readable string, representing the completed feature.
    *
    * The resulting string will be in the format: `feature_completed([(x, y, segment), ...])`
    *
    * @param feature
    *   The feature name (e.g., "city", "road").
    * @param connectedFeatures
    *   A set of tuples representing positions and corresponding tile segments.
    * @return
    *   A formatted string representing the completed feature and its associated segments.
    */
  def stringifyConnectedFeatures: (String, Set[(Position, TileSegment)]) => String =
    (feature: String, connectedFeatures: Set[(Position, TileSegment)]) =>
      s"${feature}_completed([${connectedFeatures.map((position, segment) => s"(${position.x}, ${position.y}, ${segment.toString.toLowerCase})").mkString(", ")}])"

}
