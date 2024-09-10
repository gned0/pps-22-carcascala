package carcassonne.model

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.Position
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CarcassonneBoardSuite extends AnyFunSuite with Matchers {

  test("A tile can be placed on an empty map") {
    val map = new CarcassonneBoard()
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
    val map = new CarcassonneBoard()
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
    an[IllegalArgumentException] should be thrownBy map.placeTile(tile2, position)
  }

  test("A tile can be placed next to an existing tile with matching edges") {
    val map = new CarcassonneBoard()
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
    val map = new CarcassonneBoard()
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
    an[IllegalArgumentException] should be thrownBy map.placeTile(tile2, Position(1, 0))
  }

  test("getConnectedFeature should return an empty set on an empty board") {
    val map = new CarcassonneBoard()
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

    map.getConnectedFeature(tile, TileSegment.N) shouldBe Set()
  }

  test("getConnectedFeature should return the feature for a single tile") {
    val map = new CarcassonneBoard()
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
    map.getConnectedFeature(tile, TileSegment.N) shouldBe Set((Position(0, 0), TileSegment.N))
    map.getConnectedFeature(tile, TileSegment.C) shouldBe Set((Position(0, 0), TileSegment.C), (Position(0, 0), TileSegment.E))
    map.getConnectedFeature(tile, TileSegment.S).map((pos, segment) => (map.getTile(pos).get.imagePath, segment)) shouldBe
      Set(
        (tile, TileSegment.S),
        (tile, TileSegment.SW),
        (tile, TileSegment.SE),
        (tile, TileSegment.SW),
        (tile, TileSegment.W),
        (tile, TileSegment.NW)
      ).map((tile, segment) => (tile.imagePath, segment))
  }

  test("getConnectedFeature should return connected features for adjacent tiles with matching edges") {
    val map = new CarcassonneBoard()
    val tile1 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.City,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.City,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test1.png"
    )
    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.City,
        TileSegment.NW -> SegmentType.City,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test2.png"
    )

    map.placeTile(tile1, Position(0, 0)) shouldBe true
    map.placeTile(tile2, Position(1, 0)) shouldBe true

    val feature = map.getConnectedFeature(tile1, TileSegment.E)
    val expectedFeature = Set(
      (Position(0, 0), TileSegment.E),
      (Position(0, 0), TileSegment.NE),
      (Position(0, 0), TileSegment.N),
      (Position(1, 0), TileSegment.W),
      (Position(1, 0), TileSegment.NW),
      (Position(1, 0), TileSegment.N),

    )

    feature shouldBe expectedFeature
  }

  test("getConnectedFeature should handle complex connections across multiple tiles") {
    val map = new CarcassonneBoard()
    val tile1 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Road,
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
        TileSegment.NE -> SegmentType.RoadEnd,
        TileSegment.SW -> SegmentType.RoadEnd,
        TileSegment.SE -> SegmentType.RoadEnd,
        TileSegment.C -> SegmentType.Road
      ),
      "test2.png"
    )
    val tile3 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Road,
        TileSegment.E -> SegmentType.City,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test3.png"
    )
    val tile4 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.RoadEnd
      ),
      "test4.png"
    )

    map.placeTile(tile1, Position(0, 0)) shouldBe true
    map.placeTile(tile2, Position(1, 0)) shouldBe true
    map.placeTile(tile3, Position(0, 1)) shouldBe true
    map.placeTile(tile4, Position(2, 0))

    val featureRoad = map.getConnectedFeature(tile1, TileSegment.E).map((pos, segment) => (map.getTile(pos).get.imagePath, segment))
    val expectedFeatureRoad = Set(
      (tile1, TileSegment.E),
      (tile1, TileSegment.C),
      (tile1, TileSegment.S),
      (tile2, TileSegment.W),
      (tile2, TileSegment.C),
      (tile2, TileSegment.E),
      (tile3, TileSegment.N),
      (tile3, TileSegment.C),
      (tile4, TileSegment.W)
    ).map((tile, segment) => (tile.imagePath, segment))

    featureRoad shouldBe expectedFeatureRoad


    val featureField = map.getConnectedFeature(tile1, TileSegment.SE).map((pos, segment) => (map.getTile(pos).get.imagePath, segment))
    val expectedFeatureField = Set(
      (tile1, TileSegment.SE),
      (tile3, TileSegment.NE)
    ).map((tile, segment) => (tile.imagePath, segment))

    featureField shouldBe expectedFeatureField


    val featureRoadEnd = map.getConnectedFeature(tile2, TileSegment.E).map((pos, segment) => (map.getTile(pos).get.imagePath, segment))
    val expectedFeatureRoadEnd = Set(
      (tile1, TileSegment.S),
      (tile1, TileSegment.C),
      (tile1, TileSegment.E),
      (tile2, TileSegment.W),
      (tile2, TileSegment.C),
      (tile2, TileSegment.E),
      (tile3, TileSegment.N),
      (tile3, TileSegment.C),
      (tile4, TileSegment.W)
    ).map((tile, segment) => (tile.imagePath, segment))

    featureRoadEnd shouldBe expectedFeatureRoadEnd
  }
}