package carcassonne.model

import carcassonne.controller.GameMapController
import carcassonne.model.{GameMap, Position}
import carcassonne.view.GameMapView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.shouldBe

/**
 * Test suite for the `GameMapController` class.
 * This suite contains unit tests to verify the behavior of the `GameMapController`.
 */
class GameMapControllerSuite extends AnyFunSuite:

  /**
   * Test to verify that the controller is added as an observer to the view upon initialization.
   */
  test("initialize should add the controller as an observer to the view") {
    val model = GameMatch(List(Player(0, "test", 0, 0, Color.Red), Player(1, "test2", 0, 0, Color.Blue)), GameMap(), TileDeck())
    val view = GameMapView()
    val controller = GameMapController(model, view)

    controller.initialize()
    view.getObservers.contains(controller) shouldBe true
  }

  /**
   * Test to verify that the `placeTile` method places a tile in the model at the specified position.
   */
  test("placeTile should place a tile in the model") {
    val map = GameMap()
    val model = GameMatch(List(Player(0, "test", 0, 0, Color.Red), Player(1, "test2", 0, 0, Color.Blue)), map, TileDeck())
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)
    val gameTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.Road)

    controller.placeTile(gameTile, position)
    map.getTileMap.get.contains(position) shouldBe true
  }

  /**
   * Test to verify that the `receiveTilePlacementAttempt` method calls `placeTile` with the correct position and tile.
   */
  test("receiveTilePlacementAttempt should call placeTile with the correct position and tile") {
    val map = GameMap()
    val model = GameMatch(List(Player(0, "test", 0, 0, Color.Red), Player(1, "test2", 0, 0, Color.Blue)), map, TileDeck())
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)
    val gameTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.Road)

    controller.receiveTilePlacementAttempt(gameTile, position)
    map.getTileMap.get.contains(position) shouldBe true
  }