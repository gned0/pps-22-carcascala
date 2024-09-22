package carcassonne.model

import carcassonne.model.game.Player
import carcassonne.util.{Color, PlayerColor}
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color as FXColor
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PlayerSuite extends AnyFunSuite with Matchers {

  test("Player should start with 0 score") {
    val player = Player(1, "Alice", Color.Red)
    player.getScore should be(0)
  }

  test("Player should increase score by given points") {
    val player = Player(1, "Alice", Color.Red)
    player.addScore(10)
    player.getScore should be(10)

    player.addScore(5)
    player.getScore should be(15)
  }

  test("Player should have 7 followers initially") {
    val player = Player(1, "Alice", Color.Red)
    player.getFollowers should be(7)
  }

  test("Player can place follower if followers are available") {
    val player = Player(1, "Alice", Color.Red)
    player.placeFollower() should be(true)
    player.getFollowers should be(6)
  }

  test("Player cannot place follower if no followers are available") {
    val player = Player(1, "Alice", Color.Red)

    // Place all followers
    for (_ <- 1 to 7) {
      player.placeFollower() should be(true)
    }

    // Now all followers are placed
    player.placeFollower() should be(false)
    player.getFollowers should be(0)
  }

  test("Player can return a follower") {
    val player = Player(1, "Alice", Color.Red)

    // Place and return a follower
    player.placeFollower() should be(true)
    player.getFollowers should be(6)

    player.returnFollower()
    player.getFollowers should be(7)
  }

  test("Returning a follower when none are placed does nothing") {
    val player = Player(1, "Alice", Color.Red)
    player.getFollowers should be(7)

    player.returnFollower()
    player.getFollowers should be(7) // No change since no followers were placed
  }
}
