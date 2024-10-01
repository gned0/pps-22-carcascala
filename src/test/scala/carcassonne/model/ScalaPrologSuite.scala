package carcassonne.model

import alice.tuprolog.*
import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameState, Player}
import carcassonne.model.scalaprolog.PrologProcessing
import carcassonne.model.scalaprolog.ScalaPrologEngine.{*, given}
import carcassonne.model.tile.{GameTile, SegmentType, TileDeck, TileSegment}
import carcassonne.util.{Color, Position}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import carcassonne.model.scalaprolog.PrologProcessing.*

class ScalaPrologSuite extends AnyFunSuite with Matchers:

  test("Test check City feature complete") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.City,
        TileSegment.S -> SegmentType.City,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.City,
        TileSegment.C -> SegmentType.Field
      ),
      "test.png"
    )
    map.placeTile(tile, Position(10, 10))

    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.City,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Field
      ),
      "test.png"
    )
    map.placeTile(tile2, Position(10, 11))

    val tile3 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.City,
        TileSegment.W -> SegmentType.City,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Field
      ),
      "test.png"
    )
    map.placeTile(tile3, Position(11, 10))

    val tile4 = GameTile(
      Map(
        TileSegment.N -> SegmentType.City,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.City,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Field
      ),
      "test.png"
    )
    map.placeTile(tile4, Position(11, 11))

    game.placeFollower(Position(11, 10), TileSegment.S, player1)
    val connectedFeatures = map.getConnectedFeature(Position(11, 10), TileSegment.S)
    val completed = checkCityCompleted(map, connectedFeatures)
    completed should be(true)

    game.placeFollower(Position(10, 10), TileSegment.E, player1)
    val connectedFeatures2 = map.getConnectedFeature(Position(10, 10), TileSegment.E)
    val completed2 = checkCityCompleted(map, connectedFeatures)
    completed2 should be(true)
  }

  test("Test check Road feature complete") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Road,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.RoadEnd
      ),
      "test.png"
    )
    map.placeTile(tile, Position(10, 10))

    val tile2 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Road,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Road,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Road
      ),
      "test.png"
    )
    map.placeTile(tile2, Position(11, 10))

    val tile3 = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Road,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.RoadEnd
      ),
      "test.png"
    )
    map.placeTile(tile3, Position(11, 9))

    game.placeFollower(Position(10, 10), TileSegment.E, player1)
    val connectedFeatures = map.getConnectedFeature(Position(10, 10), TileSegment.E)
    val completed = checkRoadCompleted(map, connectedFeatures)
    completed should be(true)
  }

  test("Test check Monastery feature complete") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile(
      Map(
        TileSegment.N -> SegmentType.Field,
        TileSegment.E -> SegmentType.Field,
        TileSegment.S -> SegmentType.Field,
        TileSegment.W -> SegmentType.Field,
        TileSegment.NW -> SegmentType.Field,
        TileSegment.NE -> SegmentType.Field,
        TileSegment.SW -> SegmentType.Field,
        TileSegment.SE -> SegmentType.Field,
        TileSegment.C -> SegmentType.Monastery
      ),
      "test.png"
    )
    for {
      x <- 9 to 11
      y <- 9 to 11
    } map.placeTile(tile, Position(x, y))

    game.placeFollower(Position(10, 10), TileSegment.C, player1)
    val connectedFeatures = map.getConnectedFeature(Position(10, 10), TileSegment.C)
    val completed = checkMonasteryCompleted(map, connectedFeatures)
    completed should be(true)

    game.placeFollower(Position(11, 11), TileSegment.C, player1)
    val connectedFeatures2 = map.getConnectedFeature(Position(11, 11), TileSegment.C)
    val completed2 = checkMonasteryCompleted(map, connectedFeatures2)
    completed2 should be(false)
  }
