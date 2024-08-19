import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scalafx.scene.layout.Region

class GameMapControllerSuite extends AnyFunSuite with Matchers:

  test("Controller places tile in model on view update") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    controller.initialize()

    val position = Position(100, 100)
    view.tileClicked(position, new Region())

    model.getTileMap.get.contains(position) should be(true)
  }

  test("Controller handles invalid tile placement") {
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    controller.initialize()

    // Simulate invalid placement
    val invalidPosition = Position(-1, -1)

    an [IllegalArgumentException] should be thrownBy view.tileClicked(invalidPosition, new Region())
  }