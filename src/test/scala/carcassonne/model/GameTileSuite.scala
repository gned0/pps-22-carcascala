package carcassonne.model

import carcassonne.model.tile.{GameTile, GameTileFactory, SegmentType, TileSegment}
import carcassonne.model.tile.TileSegment.N
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class GameTileSuite extends AnyFunSuite with Matchers:

  test("A Tile should be created with correct segments") {
    val tile = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.City,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.City
      ),
      "test.png"
    )
    tile.segments(TileSegment.N) shouldBe SegmentType.City
    tile.segments(TileSegment.E) shouldBe SegmentType.Road
    tile.segments(TileSegment.S) shouldBe SegmentType.Field
    tile.segments(TileSegment.W) shouldBe SegmentType.City
    tile.segments(TileSegment.C) shouldBe SegmentType.City
  }

  test("A Tile should rotate clockwise correctly") {
    val tile = GameTile(
      Map(
        TileSegment.NW -> SegmentType.Field, TileSegment.N -> SegmentType.Field, TileSegment.NE -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road, TileSegment.C -> SegmentType.Road, TileSegment.E -> SegmentType.Road,
        TileSegment.SW -> SegmentType.Field, TileSegment.S -> SegmentType.Field, TileSegment.SE -> SegmentType.Field
      ),
      "test.png"
    )
    val rotatedTile = tile.rotateClockwise
    rotatedTile.segments(TileSegment.N) shouldBe SegmentType.Road
    rotatedTile.segments(TileSegment.E) shouldBe SegmentType.Field
    rotatedTile.segments(TileSegment.S) shouldBe SegmentType.Road
    rotatedTile.segments(TileSegment.W) shouldBe SegmentType.Field
    rotatedTile.segments(TileSegment.C) shouldBe SegmentType.Road
  }

  test("A Tile should rotate counter-clockwise correctly") {
    val tile = GameTile(
      Map(
        TileSegment.NW -> SegmentType.Field, TileSegment.N -> SegmentType.Field, TileSegment.NE -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road, TileSegment.C -> SegmentType.Road, TileSegment.E -> SegmentType.Road,
        TileSegment.SW -> SegmentType.Field, TileSegment.S -> SegmentType.Field, TileSegment.SE -> SegmentType.Field
      ),
      "test.png"
    )
    val rotatedTile = tile.rotateCounterClockwise
    rotatedTile.segments(TileSegment.N) shouldBe SegmentType.Road
    rotatedTile.segments(TileSegment.E) shouldBe SegmentType.Field
    rotatedTile.segments(TileSegment.S) shouldBe SegmentType.Road
    rotatedTile.segments(TileSegment.W) shouldBe SegmentType.Field
    rotatedTile.segments(TileSegment.C) shouldBe SegmentType.Road
  }

  test("The start tile should have correct segments") {
    GameTileFactory.createStartTile().segments(TileSegment.N) shouldBe SegmentType.City
    GameTileFactory.createStartTile().segments(TileSegment.E) shouldBe SegmentType.Road
    GameTileFactory.createStartTile().segments(TileSegment.S) shouldBe SegmentType.Field
    GameTileFactory.createStartTile().segments(TileSegment.W) shouldBe SegmentType.Road
    GameTileFactory.createStartTile().segments(TileSegment.C) shouldBe SegmentType.Road
  }

  test("GameTile should be correctly deserialized from JSON") {
    val jsonString = """
      {
        "segments": {
          "NW": "Field", "N": "City", "NE": "Field",
          "W": "Road", "C": "Road", "E": "Road",
          "SW": "Field", "S": "Field", "SE": "Field"
        },
        "imagePath": "CastleSideRoad.png"
      }
    """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]
    tile.imagePath shouldBe "CastleSideRoad.png"
    tile.segments(TileSegment.N) shouldBe SegmentType.City
    tile.segments(TileSegment.E) shouldBe SegmentType.Road
    tile.segments(TileSegment.S) shouldBe SegmentType.Field
    tile.segments(TileSegment.W) shouldBe SegmentType.Road
    tile.segments(TileSegment.C) shouldBe SegmentType.Road
  }