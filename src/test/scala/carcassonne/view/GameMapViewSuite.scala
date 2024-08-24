package carcassonne.view

import carcassonne.model.{EdgeType, GameTile, Position}
import carcassonne.observers.ObserverGameView
import carcassonne.view.GameMapView
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.shouldBe
import scalafx.Includes.jfxNode2sfx
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex, sfxGridPane2jfx}
import scalafx.scene.layout.{GridPane, Region}
import scalafx.scene.text.Text

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
   * Test to verify that the `checkClickedTile` method notifies observers of a tile placement attempt.
   */
  test("checkClickedTile should notify observers of tile placement attempt") {
    val view = GameMapView()
    val position = Position(1, 1)
    val placedTile = new Region()
    var notified = false

    view.addObserver(new ObserverGameView[GameMapView] {
      override def receiveTilePlacementAttempt(gameTile: GameTile, pos: Position): Unit = {
        notified = true
      }
    })

    view.checkClickedTile(position, placedTile)
    notified shouldBe true
  }