package carcassonne.model

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GameMapSuite extends AnyFunSuite with Matchers {

  test("A tile can be placed on an empty map") {
    val map = new GameMap()
    val tile = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test.png"
    )
    val position = Position(0, 0)

    map.placeTile(tile, position) shouldBe true
    map.getTile(position) shouldBe Some(tile)
  }

  test("A tile cannot be placed on an occupied position") {
    val map = new GameMap()
    val tile1 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test1.png"
    )
    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test2.png"
    )
    val position = Position(0, 0)

    map.placeTile(tile1, position) shouldBe true
    an [IllegalArgumentException] should be thrownBy map.placeTile(tile2, position)
  }

  test("A tile can be placed next to an existing tile with matching edges") {
    val map = new GameMap()
    val tile1 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test1.png"
    )
    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test2.png"
    )

    map.placeTile(tile1, Position(0, 0)) shouldBe true
    map.placeTile(tile2, Position(1, 0)) shouldBe true
  }

  test("A tile cannot be placed next to an existing tile with mismatching edges") {
    val map = new GameMap()
    val tile1 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test1.png"
    )
    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.City,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test2.png"
    )

    map.placeTile(tile1, Position(0, 0)) shouldBe true
    an [IllegalArgumentException] should be thrownBy map.placeTile(tile2, Position(1, 0))
  }
}