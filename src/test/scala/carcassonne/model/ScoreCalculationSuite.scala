package carcassonne.model

import carcassonne.model.board.{CarcassonneBoard, Position}
import carcassonne.model.game.{Color, GameMatch, Player, ScoreCalculator}
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.view.GameMapView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class ScoreCalculationSuite extends AnyFunSuite with Matchers {

  test("Test calculate City points") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameMatch(List(player1, player2), map, TileDeck())

    val tile = GameTile.startTile
    val jsonString = """
      {
        "segments": {
          "NW": "City", "N": "City", "NE": "City",
          "W": "City", "C": "City", "E": "City",
          "SW": "Field", "S": "Field", "SE": "Field"
        },
        "imagePath": "CityOneSideField.png"
      }
    """
    val json = Json.parse(jsonString)
    val tile2 = json.as[GameTile]

    game.placeTile(tile2.rotateClockwise.rotateClockwise, Position(10, 9))
    game.placeTile(tile2.rotateClockwise.rotateClockwise, Position(11, 9))


    game.placeTile(tile.rotateClockwise, Position(9, 9))
    game.placeTile(tile.rotateCounterClockwise, Position(12, 9))
    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile, Position(11, 10))


    game.placeFollower(tile, TileSegment.N, player1)
    ScoreCalculator().calculateCityPoints(TileSegment.N, Position(10, 10), map) shouldBe 12
  }

  test("Test calculate Road points") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameMatch(List(player1, player2), map, TileDeck())

    val tile = GameTile.startTile
    val jsonString =
      """
        {
          "segments": {
            "NW": "Field", "N": "Field", "NE": "Field",
            "W": "Field", "C": "RoadEnd", "E": "Field",
            "SW": "Field", "S": "Road", "SE": "Field"
          },
          "imagePath": "RoadEnd.png"
        }
      """
    val json = Json.parse(jsonString)
    val tile2 = json.as[GameTile]

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile2.rotateClockwise, Position(11, 10))
    game.placeTile(tile2.rotateCounterClockwise, Position(9, 10))

    game.placeFollower(tile, TileSegment.C, player1)
    ScoreCalculator().calculateRoadPoints(TileSegment.C, Position(10, 10), map) shouldBe 3
  }

  test("Test calculate circular Road points") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameMatch(List(player1, player2), map, TileDeck())

    val tile = GameTile.startTile
    val jsonString =
      """
        {
          "segments": {
            "NW": "Field", "N": "Field", "NE": "Field",
            "W": "Road", "C": "Road", "E": "Field",
            "SW": "Field", "S": "Road", "SE": "Field"
          },
          "imagePath": "RoadCurve.png"
        }
      """
    val json = Json.parse(jsonString)
    val tile2 = json.as[GameTile]

    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(10, 10))
    game.placeTile(tile.rotateClockwise, Position(11, 9))
    game.placeTile(tile.rotateCounterClockwise, Position(9, 9))
    game.placeTile(tile, Position(10, 8))

    game.placeTile(tile2.rotateClockwise, Position(11, 10))
    game.placeTile(tile2, Position(11, 8))
    game.placeTile(tile2.rotateClockwise.rotateClockwise, Position(9, 10))
    game.placeTile(tile2.rotateCounterClockwise, Position(9, 8))

    game.placeFollower(tile, TileSegment.C, player1)
    ScoreCalculator().calculateRoadPoints(TileSegment.C, Position(10, 10), map) shouldBe 8
  }

  test("Test calculate Field points") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameMatch(List(player1, player2), map, TileDeck())

    val tile = GameTile.startTile
    val jsonString =
      """
          {
            "segments": {
              "NW": "Field", "N": "Field", "NE": "Field",
              "W": "Road", "C": "Road", "E": "Road",
              "SW": "Field", "S": "Field", "SE": "Field"
            },
            "imagePath": "HorizontalRoad.png"
          }
        """
    val json = Json.parse(jsonString)
    val tile2 = json.as[GameTile]

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(10, 9))

    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(11, 10))

    game.placeTile(tile, Position(12, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(12, 9))

    game.placeFollower(tile, TileSegment.NE, player1)
    ScoreCalculator().calculateFieldPoints(TileSegment.NE, Position(10, 10), map) shouldBe 6
  }


}