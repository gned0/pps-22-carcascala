package carcassonne.model

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
 * @param color The color associated with the player.
 */
case class Player(playerId: Int, name: String, color: Color) {
  var score: Int = 0
  private var placedFollowers: Int = 0
  private val maxFollowers: Int = 7

  /**
   * Checks if the player has at least one free follower.
   *
   * @return `true` if there is at least one free follower, otherwise `false`.
   */
  def hasFreeFollower: Boolean = placedFollowers < maxFollowers

  /**
   * Increases the player's score by a given amount.
   *
   * @param points The number of points to add to the player's score.
   */
  def addScore(points: Int): Unit = {
    score += points
  }

  /**
   * Returns the current score of the player.
   *
   * @return The current score.
   */
  def getScore: Int = score

  /**
   * Places a follower if possible, incrementing the count of followers placed.
   *
   * @return `true` if the follower was placed successfully, otherwise `false`.
   */
  def placeFollower(): Boolean = {
    if (hasFreeFollower) {
      placedFollowers += 1
      true
    } else {
      false
    }
  }

  /**
   * Returns a follower, decrementing the count of followers placed.
   */
  def returnFollower(): Unit = {
    if (placedFollowers > 0) {
      placedFollowers -= 1
    }
  }

  /**
   * Gets the number of followers currently placed.
   *
   * @return The number of followers placed.
   */
  def getFollowerPlaced: Int = placedFollowers
}

