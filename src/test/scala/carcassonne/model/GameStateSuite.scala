package carcassonne.model

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameState, Player, ScoreCalculator}
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.util.{Color, Position}
import carcassonne.view.gameMatch.GameMatchBoardView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class GameStateSuite extends AnyFunSuite with Matchers {

  test("Board and player updated after City calculation") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile.createStartTile()
    val jsonString =
      """
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

    game.placeFollower(Position(10, 10), TileSegment.N, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 12
    player1.getFollowers shouldBe 7

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 0
  }

  test("Board and player updated after Road calculation") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile.createStartTile()
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

    game.placeFollower(Position(10, 10), TileSegment.C, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 3
    player1.getFollowers shouldBe 7

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 0
  }

  test("Board and player updated after Monastery calculation") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val jsonString =
      """
                    {
                      "segments": {
                        "NW": "Field", "N": "Field", "NE": "Field",
                        "W": "Field", "C": "Field", "E": "Field",
                        "SW": "Field", "S": "Field", "SE": "Field"
                      },
                      "imagePath": "Field.png"
                    }
                  """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    val jsonString2 =
      """
                {
                  "segments": {
                    "NW": "Field", "N": "Field", "NE": "Field",
                    "W": "Field", "C": "Monastery", "E": "Field",
                    "SW": "Field", "S": "Field", "SE": "Field"
                  },
                  "imagePath": "Monastery.png"
                }
              """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    game.placeTile(tile2, Position(10, 10))

    game.placeTile(tile, Position(11, 10))
    game.placeTile(tile, Position(11, 9))
    game.placeTile(tile, Position(10, 9))
    game.placeTile(tile, Position(9, 9))
    game.placeTile(tile, Position(9, 10))
    game.placeTile(tile, Position(9, 11))
    game.placeTile(tile, Position(10, 11))
    game.placeTile(tile, Position(11, 11))

    game.placeFollower(Position(10, 10), TileSegment.C, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 9
    player1.getFollowers shouldBe 7

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 0
  }

  test("Board and player updated after Field calculation") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val jsonString =
      """
                     {
                       "segments": {
                         "NW": "Field", "N": "City", "NE": "Field",
                         "W": "Field", "C": "Field", "E": "Field",
                         "SW": "Field", "S": "Field", "SE": "Field"
                       },
                       "imagePath": "TopCityField.png"
                     }
                   """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    val jsonString2 =
      """
                 {
                   "segments": {
                     "NW": "Field", "N": "Field", "NE": "Field",
                     "W": "Field", "C": "Field", "E": "Field",
                     "SW": "Field", "S": "Field", "SE": "Field"
                   },
                   "imagePath": "Field.png"
                 }
               """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(10, 9))

    game.placeTile(tile2, Position(11, 10))

    game.placeTile(tile, Position(12, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(12, 9))

    game.placeFollower(Position(11, 10), TileSegment.C, player1)

    game.calculateScore(true)
    player1.getScore shouldBe 6
    player1.getFollowers shouldBe 7

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 0
  }

  test("Board and player NOT updated if City is open") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile.createStartTile()
    val jsonString =
      """
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

    game.placeFollower(Position(10, 10), TileSegment.N, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 0
    player1.getFollowers shouldBe 6

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 1
  }

  test("Board and player NOT updated if Road is open") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val tile = GameTile.createStartTile()
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

    game.placeFollower(Position(10, 10), TileSegment.C, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 0
    player1.getFollowers shouldBe 6

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 1
  }

  test("Board and player NOT updated if Monastery is open") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val jsonString =
      """
                      {
                        "segments": {
                          "NW": "Field", "N": "Field", "NE": "Field",
                          "W": "Field", "C": "Field", "E": "Field",
                          "SW": "Field", "S": "Field", "SE": "Field"
                        },
                        "imagePath": "Field.png"
                      }
                    """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    val jsonString2 =
      """
                  {
                    "segments": {
                      "NW": "Field", "N": "Field", "NE": "Field",
                      "W": "Field", "C": "Monastery", "E": "Field",
                      "SW": "Field", "S": "Field", "SE": "Field"
                    },
                    "imagePath": "Monastery.png"
                  }
                """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    game.placeTile(tile2, Position(10, 10))

    game.placeTile(tile, Position(11, 10))
    game.placeTile(tile, Position(11, 9))
    game.placeTile(tile, Position(10, 9))

    game.placeFollower(Position(10, 10), TileSegment.C, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 0
    player1.getFollowers shouldBe 6

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 1
  }

  test("Board and player NOT updated if Field is not endgame") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val jsonString =
      """
                       {
                         "segments": {
                           "NW": "Field", "N": "City", "NE": "Field",
                           "W": "Field", "C": "Field", "E": "Field",
                           "SW": "Field", "S": "Field", "SE": "Field"
                         },
                         "imagePath": "TopCityField.png"
                       }
                     """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    val jsonString2 =
      """
                   {
                     "segments": {
                       "NW": "Field", "N": "Field", "NE": "Field",
                       "W": "Field", "C": "Field", "E": "Field",
                       "SW": "Field", "S": "Field", "SE": "Field"
                     },
                     "imagePath": "Field.png"
                   }
                 """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(10, 9))

    game.placeTile(tile2, Position(11, 10))

    game.placeTile(tile, Position(12, 10))
    game.placeTile(tile.rotateClockwise.rotateClockwise, Position(12, 9))

    game.placeFollower(Position(11, 10), TileSegment.C, player1)

    game.calculateScore(false)
    player1.getScore shouldBe 0
    player1.getFollowers shouldBe 6

    val followerTiles = map.getTileMap.get.filter((_, tile) => tile.getFollowerMap.nonEmpty)
    followerTiles.size shouldBe 1
  }

  test("Tile, player and board should be initialized") {
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    game.getPlayers should contain theSameElementsAs List(
      player1,
      player2
    )

    game.getBoard.equals(map) shouldBe true

    game.initializeFirstPlayer()
    game.getBoard.getTile(Position(500, 500)).get.equals(GameTile.createStartTile())
  }
}
