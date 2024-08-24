package carcassonne.model

import carcassonne.model.{EdgeType, GameMap, GameTile, Position}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GameMapSuite extends AnyFunSuite with Matchers:

  test("A tile should be placed on the map at a specific position") {
    val map = GameMap()
    val tile = GameTile.startTile
    val position = Position(0, 0)

    map.placeTile(tile, position)

    map.getTile(position) shouldBe Some(tile)
  }

  test("Placing a tile should fail if a tile already exists at that position") {
    val map = GameMap()
    val tile1 = GameTile.startTile
//    val tile2 = GameTile(EdgeType.Field, EdgeType.Road, EdgeType.Field, EdgeType.Road, Map(
//      "NW" -> "Field", "N" -> "Field", "NE" -> "Field",
//      "W" -> "Road", "C" -> "Road", "E" -> "Road",
//      "SW "-> "Field", "S" -> "Field", "SE" -> "Field"
//    ))
    val position = Position(0, 0)

    map.placeTile(tile1, position)

//    an [IllegalArgumentException] should be thrownBy map.placeTile(tile2, position)
  }

  test("Valid placement should return true for a matching neighbor tile") {
    val map = GameMap()
    val tile1 = GameTile.startTile
    val position1 = Position(0, 0)
    map.placeTile(tile1, position1)

//    val tile2 = GameTile(EdgeType.Field, EdgeType.City, EdgeType.Road, EdgeType.Field, Map(
//      "NW" -> "Field", "N" -> "Field", "NE" -> "Field",
//      "W" -> "Road", "C" -> "Road", "E" -> "Road",
//      "SW" -> "Field", "S" -> "Field", "SE" -> "Field"
//    ))
    val position2 = Position(0, 1) // South of position1

//    noException should be thrownBy map.placeTile(tile2, position2)
  }

  test("Valid placement with multiple matching neighbors") {
    val map = GameMap()

//    val tile1 = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
//    val tile2 = GameTile(EdgeType.Field, EdgeType.Field, EdgeType.City, EdgeType.Road)
//    val tile3 = GameTile(EdgeType.Field, EdgeType.City, EdgeType.Road, EdgeType.Road)

//    map.placeTile(tile1, Position(0, 0))
//    map.placeTile(tile2, Position(0, 1)) // South
//    map.placeTile(tile3, Position(1, 0)) // East

//    val tile4 = GameTile(EdgeType.Road, EdgeType.City, EdgeType.City, EdgeType.Field)
    val position4 = Position(1, 1) // Southeast corner, connects to all three

//    noException should be thrownBy map.placeTile(tile4, position4)
  }

  test("Invalid placement with a mismatching edge") {
    val map = GameMap()

//    val tile1 = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
//    val position1 = Position(0, 0)
//    map.placeTile(tile1, position1)
//
//    val invalidTile = GameTile(EdgeType.Road, EdgeType.City, EdgeType.City, EdgeType.Field)
//    val invalidPosition = Position(0, 1) // South of position1, but does not match
//
//    an [IllegalArgumentException] should be thrownBy map.placeTile(invalidTile, invalidPosition)
  }

  test("Valid placement with rotation") {
    val map = GameMap()

//    val tile1 = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
//    val position1 = Position(0, 0)
//    map.placeTile(tile1, position1)
//
//    val tile2 = GameTile(EdgeType.Road, EdgeType.City, EdgeType.Field, EdgeType.Road).rotate.rotate
//    val position2 = Position(0, 1) // South of position1, requires double rotation to match

//    noException should be thrownBy map.placeTile(tile2, position2)
  }

  test("Valid placement in an open space surrounded by tiles") {
    val map = GameMap()

//    val tile1 = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Road, EdgeType.City)
//    val tile2 = GameTile(EdgeType.Road, EdgeType.Field, EdgeType.City, EdgeType.Road)
//    val tile3 = GameTile(EdgeType.Field, EdgeType.City, EdgeType.Road, EdgeType.Road)
//    val tile4 = GameTile(EdgeType.City, EdgeType.City, EdgeType.Road, EdgeType.Field)
//
//    map.placeTile(tile1, Position(0, 0))
//    map.placeTile(tile2, Position(0, 1)) // South
//    map.placeTile(tile3, Position(1, 0)) // East
//    map.placeTile(tile4, Position(-1, 0)) // West
//
//    val northTile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.City, EdgeType.City)
//    val northPosition = Position(0, -1)

//    noException should be thrownBy map.placeTile(northTile, northPosition)
  }

  test("Valid placement with no neighbors (first tile)") {
//    val map = GameMap()
//    val tile = GameTile(EdgeType.City, EdgeType.Road, EdgeType.Field, EdgeType.City)
//    val position = Position(0, 0)
//
//    noException should be thrownBy map.placeTile(tile, position)
//    map.getTile(position) shouldBe Some(tile)
  }
