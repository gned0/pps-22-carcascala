package carcassonne.view

import carcassonne.model.{GameTile, Position}
import carcassonne.observers.ObserverGameView
import carcassonne.view.GameMapView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.shouldBe
import scalafx.Includes.jfxNode2sfx
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex, sfxGridPane2jfx}
import scalafx.scene.layout.{GridPane, Region}

/**
 * Test suite for the `GameMapView` class.
 * This suite contains unit tests to verify the behavior of the `GameMapView`.
 */
class GameMapViewSuite extends AnyFunSuite:

  /**
   * Test to verify that the `createPlaceholderTile` method creates a tile with the correct properties.
   */
  test("createPlaceholderTile should create a tile with correct properties") {
    val view = GameMapView()
    val position = Position(1, 1)
    val placeholderTile = view.createPlaceholderTile(position)

    placeholderTile.prefWidth.value shouldBe 100
    placeholderTile.prefHeight.value shouldBe 100
    placeholderTile.styleClass.contains("placeholderTile") shouldBe true
  }

  /**
   * Test to verify that the `placeTile` method places a tile at the correct position in the view.
   */
  test("placeTile should place a tile at the correct position") {
    val view = GameMapView()
    val position = Position(1, 1)
    val placedTile = new Region()
    val tiles = Map.empty[Position, GameTile]

    view.placeTile(position, placedTile, tiles)

    view.getChildren.contains(placedTile) shouldBe true
  }

  /**
   * Test to verify that the `createNewPlaceholders` method creates new placeholders around the last placed tile.
   */
  test("createNewPlaceholders should create new placeholders around the last placed tile") {
    val view = GameMapView()
    val position = Position(2, 2)
    val placedTile = new Region()
    val tiles = Map.empty[Position, GameTile]

    view.placeTile(position, placedTile, tiles)
    view.createNewPlaceholders(tiles)

    val expectedPositions = Seq(Position(2, 2), Position(1, 2), Position(3, 2), Position(2, 1), Position(2, 3))
    var expectedTiles: Seq[Position] = List()
    view.getChildren.forEach(node =>
      var pos = Position(GridPane.getColumnIndex(node), GridPane.getRowIndex(node))
      if pos != Position(100, 100) then
        expectedTiles = expectedTiles :+ pos
    )

    expectedTiles.foreach(tile =>
      expectedPositions.contains(tile) shouldBe true
    )
  }

  /**
   * Test to verify that the `checkClickedTile` method notifies observers of a tile placement attempt.
   */
  test("checkClickedTile should notify observers of tile placement attempt") {
    val view = GameMapView()
    val position = Position(1, 1)
    val placedTile = new Region()
    var notified = false

//    view.addObserver(new ObserverGameView[GameMapView] {
//      override def receiveTilePlacementAttempt(pos: Position): Unit = {
//        notified = true
//      }
//    })

    view.checkClickedTile(position, placedTile)
    notified shouldBe true
  }