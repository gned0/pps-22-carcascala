package mainApplication

/**
 * Represents the different types of edges that can be found on a tile.
 *
 * `City`: A city segment.
 * `Road`: A road segment.
 * `Field`: A field segment.
 */
enum EdgeType:
  case City, Road, Field

/**
 * Represents a tile in the game.
 *
 * @param north The edge type on the northern side of the tile.
 * @param east The edge type on the eastern side of the tile.
 * @param south The edge type on the southern side of the tile.
 * @param west The edge type on the western side of the tile.
 */
case class GameTile(north: EdgeType, east: EdgeType, south: EdgeType, west: EdgeType):

  /**
   * Rotates the tile 90 degrees clockwise.
   *
   * @return A new `GameTile` instance with edges rotated clockwise.
   */
  def rotate: GameTile = GameTile(west, north, east, south)

object GameTile:

  /**
   * The starting tile of the game, with specific edges as per the game rules:
   * - North: `City`
   * - East: `Road`
   * - South: `Field`
   * - West: `Road`
   */
  val startTile: GameTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.Road)
