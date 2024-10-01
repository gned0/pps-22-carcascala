package carcassonne.model

import carcassonne.model.board.CarcassonneBoard
import carcassonne.model.game.{GameState, Player, ScoreCalculator}
import carcassonne.model.tile.{GameTile, TileDeck, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchBoard
import carcassonne.util.{Color, Position}
import carcassonne.view.gameMatch.GameMatchBoardView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json

class FollowerPlacementSuite extends AnyFunSuite with Matchers {

  test("Test segments all available") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val testObv = TestObserverFollower()
    game.addObserverBoard(testObv)

    val tile = GameTile.createStartTile()

    val jsonString2 = """
      {
        "segments": {
          "NW": "Field", "N": "Field", "NE": "Field",
          "W": "Road", "C": "Road", "E": "Field",
          "SW": "Field", "S": "Road", "SE": "Field"
        },
        "imagePath": "RoadCurve.png"
      }
    """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    val jsonString3 =
      """
          {
            "segments": {
              "NW": "Field", "N": "City", "NE": "City",
              "W": "Field", "C": "Field", "E": "City",
              "SW": "Field", "S": "Field", "SE": "Field"
            },
            "imagePath": "CastleEdge.png"
          }
        """
    val json3 = Json.parse(jsonString3)
    val tile3 = json3.as[GameTile]

    val jsonString4 =
      """
          {
            "segments": {
              "NW": "Field", "N": "City", "NE": "City",
              "W": "Road", "C": "Road", "E": "City",
              "SW": "Field", "S": "Road", "SE": "Field"
            },
            "imagePath": "CastleEdgeRoad.png"
          }
        """
    val json4 = Json.parse(jsonString4)
    val tile4 = json4.as[GameTile]

    game.placeTile(tile, Position(10, 11))
    game.placeTile(tile, Position(9, 11))
    game.placeTile(tile2.rotateClockwise, Position(11, 11))
    game.placeTile(tile3.rotateClockwise, Position(9, 10))
    game.placeTile(tile2, Position(11, 10))
    game.placeTile(tile4.rotateClockwise.rotateClockwise, Position(10, 10))

    game.sendAvailableFollowerPositions(tile4, Position(10, 10))
    testObv.getAvailSegments should contain theSameElementsAs List(
      TileSegment.NE,
      TileSegment.N,
      TileSegment.NW,
      TileSegment.E,
      TileSegment.C,
      TileSegment.W,
      TileSegment.SE,
      TileSegment.S,
      TileSegment.SW
    )
  }

  test("Test follower cannot be placed on RoadEnd") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val testObv = TestObserverFollower()
    game.addObserverBoard(testObv)

    val jsonString =
      """
        {
          "segments": {
            "NW": "Field", "N": "Road", "NE": "Field",
            "W": "Road", "C": "RoadEnd", "E": "Road",
            "SW": "Field", "S": "Road", "SE": "Field"
          },
          "imagePath": "RoadCurve.png"
        }
      """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    game.placeTile(tile, Position(10, 10))

    game.sendAvailableFollowerPositions(tile, Position(10, 10))
    testObv.getAvailSegments should not contain TileSegment.C
  }

  test("Test road already taken") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val testObv = TestObserverFollower()
    game.addObserverBoard(testObv)

    val tile = GameTile.createStartTile()

    game.placeTile(tile, Position(9, 10))
    game.placeFollower(Position(9, 10), TileSegment.W, player1)

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile, Position(11, 10))

    game.sendAvailableFollowerPositions(tile, Position(11, 10))
    testObv.getAvailSegments should contain theSameElementsAs List(
      TileSegment.NE,
      TileSegment.N,
      TileSegment.NW,
      TileSegment.SE,
      TileSegment.S,
      TileSegment.SW
    )
  }

  test("Test city already taken") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val testObv = TestObserverFollower()
    game.addObserverBoard(testObv)

    val jsonString =
      """
                  {
                    "segments": {
                      "NW": "Field", "N": "City", "NE": "Field",
                      "W": "Field", "C": "Field", "E": "City",
                      "SW": "Field", "S": "Field", "SE": "Field"
                    }
                    ,
                    "imagePath": "CastleSidesEdge.png"
                  }
              """
    val json = Json.parse(jsonString)
    val tile = json.as[GameTile]

    val jsonString2 =
      """
                      {
                        "segments": {
                          "NW": "Field", "N": "City", "NE": "City",
                          "W": "Field", "C": "Field", "E": "City",
                          "SW": "Field", "S": "Field", "SE": "Field"
                        }
                        ,
                        "imagePath": "CastleSidesEdge.png"
                      }
                  """
    val json2 = Json.parse(jsonString2)
    val tile2 = json2.as[GameTile]

    game.placeTile(tile, Position(10, 10))
    game.placeFollower(Position(10, 10), TileSegment.E, player1)

    game.placeTile(tile2.rotateCounterClockwise, Position(11, 10))
    game.placeTile(tile2.rotateClockwise.rotateClockwise, Position(11, 9))

    game.sendAvailableFollowerPositions(tile2.rotateClockwise.rotateClockwise, Position(11, 9))
    testObv.getAvailSegments should contain theSameElementsAs List(
      TileSegment.NE,
      TileSegment.N,
      TileSegment.NW,
      TileSegment.E,
      TileSegment.C,
      TileSegment.SE
    )
  }

  test("Test field already taken") {
    val deck = TileDeck()
    val map = CarcassonneBoard()
    val player1 = Player(0, "test", Color.Red)
    val player2 = Player(1, "test2", Color.Blue)
    val game = GameState(List(player1, player2), map, TileDeck())

    val testObv = TestObserverFollower()
    game.addObserverBoard(testObv)

    val tile = GameTile.createStartTile()

    game.placeTile(tile, Position(9, 10))
    game.placeFollower(Position(9, 10), TileSegment.NE, player1)

    game.placeTile(tile, Position(10, 10))
    game.placeTile(tile, Position(11, 10))

    game.sendAvailableFollowerPositions(tile, Position(11, 10))
    testObv.getAvailSegments should contain theSameElementsAs List(
      TileSegment.N,
      TileSegment.E,
      TileSegment.C,
      TileSegment.W,
      TileSegment.SE,
      TileSegment.S,
      TileSegment.SW
    )
  }
}

class TestObserverFollower extends ObserverGameMatchBoard {
  private var availSegments: List[TileSegment] = Nil
  def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    this.availSegments = availSegments

  def getAvailSegments: List[TileSegment] = availSegments

  // ignored methods
  def isTilePlaced(isTilePlaced: Boolean, position: Position): Unit = ()

  def gameEnded(players: List[Player]): Unit = ()

  def playerChanged(player: Player): Unit = ()

  def scoreCalculated(position: Position, gameTile: GameTile): Unit = ()
}
