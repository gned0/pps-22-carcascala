package carcassonne.model

import carcassonne.view.GameMapView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class GameSuite extends AnyFunSuite with Matchers {

  test("test") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", 0, 0, Color.Red)
    val player2 = Player(1, "test2", 0, 0, Color.Blue)
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


    game.placeMeeple(tile, TileSegment.N, player1)

    println(game.calculateCityPoints(TileSegment.N, Position(10, 10)))





  }


}