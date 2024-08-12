import org.scalatest.funsuite.AnyFunSuite

class CarcassonneGameTest extends AnyFunSuite {
  test("A new game should start with an empty board") {
    val game = new CarcassonneGame()
    assert(game.getBoardSize == 0)
  }

  test("Placing a tile should increase the board size") {
    val game = new CarcassonneGame()
    game.placeTile(0, 0, "City")
    assert(game.getBoardSize == 1)
  }

  test("Placing a tile at an occupied position should throw an exception") {
    val game = new CarcassonneGame()
    game.placeTile(0, 0, "City")
    assertThrows[IllegalArgumentException] {
      game.placeTile(0, 0, "Road")
    }
  }

  test("Getting a tile at a valid position should return the correct tile type") {
    val game = new CarcassonneGame()
    game.placeTile(0, 0, "City")
    assert(game.getTileAt(0, 0).contains("City"))
  }

  test("Getting a tile at an invalid position should return None") {
    val game = new CarcassonneGame()
    assert(game.getTileAt(0, 0).isEmpty)
  }
}