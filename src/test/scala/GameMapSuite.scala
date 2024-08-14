import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GameMapSuite extends AnyFunSuite with Matchers:

  test("A tile should be placed on the map at a specific position") {
    val map = GameMap()
    val tile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
    val position = Position(0, 0)

    map.placeTile(tile, position)

    map.getTile(position) shouldBe Some(tile)
  }

  test("Placing a tile should fail if a tile already exists at that position") {
    val map = GameMap()
    val tile1 = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
    val tile2 = GameTile(EdgeType.Field, EdgeType.City, EdgeType.Road, EdgeType.Field)
    val position = Position(0, 0)

    map.placeTile(tile1, position)

    an [IllegalArgumentException] should be thrownBy map.placeTile(tile2, position)
  }
