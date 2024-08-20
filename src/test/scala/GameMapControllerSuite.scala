import mainApplication.{GameMap, GameMapController, GameMapView, Position}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.shouldBe

class GameMapControllerSuite extends AnyFunSuite:

  test("initialize should add the controller as an observer to the view") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)

    controller.initialize()
    view.getObservers.contains(controller) shouldBe true
  }

  test("placeTile should place a tile in the model") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)

    controller.placeTile(position)
    model.getTileMap.get.contains(position) shouldBe true
  }

  test("receiveTilePlacementAttempt should call placeTile with the correct position") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    val position = Position(1, 1)

    controller.receiveTilePlacementAttempt(position)
    model.getTileMap.get.contains(position) shouldBe true
  }