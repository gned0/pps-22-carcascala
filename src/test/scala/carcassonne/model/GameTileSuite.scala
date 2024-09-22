package carcassonne.model

import carcassonne.model.tile.SegmentType.{City, Field, Road}
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.model.tile.TileSegment.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.*

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
        TileSegment.NW -> SegmentType.Field,
        TileSegment.N -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.C -> SegmentType.Road,
        TileSegment.E -> SegmentType.Road,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field
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
        TileSegment.NW -> SegmentType.Field,
        TileSegment.N -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.C -> SegmentType.Road,
        TileSegment.E -> SegmentType.Road,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field
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
    GameTile.createStartTile().segments(TileSegment.N) shouldBe SegmentType.City
    GameTile.createStartTile().segments(TileSegment.E) shouldBe SegmentType.Road
    GameTile.createStartTile().segments(TileSegment.S) shouldBe SegmentType.Field
    GameTile.createStartTile().segments(TileSegment.W) shouldBe SegmentType.Road
    GameTile.createStartTile().segments(TileSegment.C) shouldBe SegmentType.Road
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

  test("Center segment (C) should remain unchanged after rotation") {
    val tile = GameTile(
      Map(
        NW -> Field,
        N -> City,
        NE -> Field,
        W -> Road,
        C -> Road,
        E -> Road,
        SW -> Field,
        S -> Field,
        SE -> Field
      ),
      "test.png"
    )
    val rotatedTileClockwise = tile.rotateClockwise
    val rotatedTileCounterClockwise = tile.rotateCounterClockwise
    rotatedTileClockwise.segments(C) shouldBe SegmentType.Road
    rotatedTileCounterClockwise.segments(C) shouldBe SegmentType.Road
  }

  test("TileSegment adjacent segments should be correctly calculated") {
    N.adjacentSegments shouldBe Set(NW, NE, C)
    NE.adjacentSegments shouldBe Set(N, E)
    E.adjacentSegments shouldBe Set(NE, SE, C)
    SE.adjacentSegments shouldBe Set(E, S)
    S.adjacentSegments shouldBe Set(SE, SW, C)
    SW.adjacentSegments shouldBe Set(S, W)
    W.adjacentSegments shouldBe Set(SW, NW, C)
    NW.adjacentSegments shouldBe Set(W, N)
    C.adjacentSegments shouldBe Set(N, E, S, W)
  }

  test("TileSegment reads method should return JsError for unknown segment") {
    val unknownJson = JsString("UnknownSegment")
    val result = Json.fromJson[TileSegment](unknownJson)

    result shouldBe a[JsError]
    result.asEither.left.get.head._2.head.message should be("Unknown tile segment: UnknownSegment")
  }

  test("TileSegment reads method should parse valid segments correctly") {
    val validJson = JsString("N")
    val result = Json.fromJson[TileSegment](validJson)

    result shouldBe JsSuccess(TileSegment.N)
  }

  test("TileSegment writes method should serialize TileSegment to JSON string") {
    val tileSegment: TileSegment = TileSegment.N
    val json = Json.toJson(tileSegment)

    json shouldBe JsString("N")
  }

  test("TileSegment writes method should serialize all segments correctly") {
    val segments = Seq(
      TileSegment.N,
      TileSegment.NE,
      TileSegment.E,
      TileSegment.SE,
      TileSegment.S,
      TileSegment.SW,
      TileSegment.W,
      TileSegment.NW,
      TileSegment.C
    )
    segments.foreach { segment =>
      val json = Json.toJson(segment)
      json shouldBe JsString(segment.toString)
    }
  }

//  test("Random tile creation should generate valid segments and imagePath") {
//    val randomTile = GameTile.createRandomTile
//    randomTile.segments.keys should contain allElementsOf TileSegment.values.toSet
//    randomTile.imagePath should startWith("RandomTile")
//    randomTile.imagePath should endWith(".png")
//  }

//  test("GameTile instance should remain immutable") {
//    val tile = GameTile(
//      Map(N -> City, E -> Road, S -> Field, W -> City, NW -> Field, NE -> Field, SW -> Field, SE -> Field, C -> City),
//      "test.png"
//    )
//    val newTile = tile.rotateClockwise
//    newTile should not be theSameInstanceAs(tile)
//    newTile.segments(N) shouldBe SegmentType.Road
//    tile.segments(N) shouldBe SegmentType.City // Ensure the original tile remains unchanged
//  }
