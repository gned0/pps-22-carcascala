import mainApplication.{GameMap, GameMapController, GameMapView, Position}
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
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)

    controller.initialize()
    view.getObservers.contains(controller) shouldBe true
  }

  /**
   * Test to verify that the `placeTile` method places a tile in the model at the specified position.
   */
  test("placeTile should place a tile in the model") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)

    controller.placeTile(position)
    model.getTileMap.get.contains(position) shouldBe true
  }

  /**
   * Test to verify that the `receiveTilePlacementAttempt` method calls `placeTile` with the correct position.
   */
  test("receiveTilePlacementAttempt should call placeTile with the correct position") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)

    controller.receiveTilePlacementAttempt(position)
    model.getTileMap.get.contains(position) shouldBe true
  }