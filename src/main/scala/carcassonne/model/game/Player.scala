package carcassonne.model.game

import carcassonne.util.{Color, PlayerColor}
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color as FXColor

object Player:
  private val MaxFollowers = 7
/**
 * Represents a player in the game.
 *
 * @param playerId The unique identifier for the player.
 * @param name The name of the player.
 * @param color The color associated with the player.
 */
case class Player(playerId: Int, name: String, color: Color):
  private var score: Int = 0
  private var placedFollowers: Int = 0
  private val maxFollowers: Int = Player.MaxFollowers


  /**
   * Checks if the player has at least one free follower.
   *
   * @return `true` if there is at least one free follower, otherwise `false`.
   */
  private def hasFreeFollower: Boolean = placedFollowers < maxFollowers

  /**
   * Increases the player's score by a given amount.
   *
   * @param points The number of points to add to the player's score.
   */
  def addScore(points: Int): Unit =
    score = score + points

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
  def placeFollower(): Boolean =
    if (!hasFreeFollower) return false
    placedFollowers += 1
    true

  /**
   * Returns a follower, decrementing the count of followers placed.
   */
  def returnFollower(): Unit =
    if placedFollowers > 0 then placedFollowers = placedFollowers - 1

  def getPlayerColor: ColorAdjust = color.getColorAdjust

  def getSFXColor: FXColor = color.getSFXColor


