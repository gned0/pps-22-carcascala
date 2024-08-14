import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GameTileSuite extends AnyFunSuite with Matchers:

  test("A Tile should be created with correct edges") {
    val tile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
    tile.north shouldBe EdgeType.City
    tile.east shouldBe EdgeType.Road
    tile.south shouldBe EdgeType.Field
    tile.west shouldBe EdgeType.City
  }

  test("A Tile should rotate correctly") {
    val tile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
    val rotatedTile = tile.rotate
    rotatedTile.north shouldBe EdgeType.City
    rotatedTile.east shouldBe EdgeType.City
    rotatedTile.south shouldBe EdgeType.Road
    rotatedTile.west shouldBe EdgeType.Field
  }

  test("The start tile should have correct edges") {
    GameTile.startTile.north shouldBe EdgeType.City
    GameTile.startTile.east shouldBe EdgeType.Road
    GameTile.startTile.south shouldBe EdgeType.Field
    GameTile.startTile.west shouldBe EdgeType.Road
  }
