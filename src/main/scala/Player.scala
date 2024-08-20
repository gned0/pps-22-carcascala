/**
 * Represents the different colors that a player can choose.
 */
enum Color:
  case Black, Red, Yellow, Green, Blue

/**
 * Represents a player in the game.
 *
 * @param playerId The unique identifier for the player.
 * @param name The name of the player.
 * @param score The player's current score, initialized to 0.
 * @param followerPlaced The number of follower already placed.
 * @param color The color associated with the player.
 */
case class Player(
  val playerId: Int,
  val name: String,
  var score: Int = 0,
  var followerPlaced: Int,
  val color: Color
) {
  private val maxFollowers: Int = 7
  /**
   * Checks if the player has at least one free follower.
   *
   * @return `true` if there is at least one free follower, otherwise `false`.
   */
  def hasFreeFollower: Boolean = {
    followerPlaced < maxFollowers
  }
}
