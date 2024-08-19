import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scalafx.scene.layout.Region

class GameMapViewSuite extends AnyFunSuite with Matchers:

  test("Initial placeholder tile creation") {
    val view = GameMapView()
    view.getChildren.size should be(1)
  }

  test("Tile placement and placeholder creation") {
    val view = GameMapView()
    val initialTile = new Region()
    view.tileClicked(Position(100, 100), initialTile)

    view.getTilesPlaced.get.size should be(1)
    view.getTilesPlaced.get.contains(Position(100, 100)) should be(true)
    view.getChildren.size should be(5) // 1 initial + 4 new placeholders
  }

  test("Observer notification on tile click") {
    val view = GameMapView()
    var notified = false
    val observer = new Observer[GameMapView] {
      override def receiveUpdate(subject: GameMapView): Unit = notified = true
    }
    view.addObserver(observer)

    val initialTile = new Region()
    view.tileClicked(Position(100, 100), initialTile)

    notified should be(true)
  }