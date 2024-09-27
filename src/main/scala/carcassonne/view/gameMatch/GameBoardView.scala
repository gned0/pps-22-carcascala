package carcassonne.view.gameMatch

import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.Image
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import carcassonne.util.Color as SFXColor
import scalafx.scene.transform.Scale

/**
 * Represents the game board view in the Carcassonne game.
 * This class extends `StackPane` and provides functionality for handling mouse events
 * such as dragging and zooming.
 */
class GameBoardView extends StackPane:

  /** Initial X position of the mouse when pressed. */
  private var initialX: Double = 0.0

  /** Initial Y position of the mouse when pressed. */
  private var initialY: Double = 0.0

  /** Current zoom factor of the game board. */
  private var zoomFactor: Double = 1.0

  /** Increment value for zooming in and out. */
  private val zoomIncrement: Double = 0.1

  /** Minimum zoom level allowed. */
  private val minZoom: Double = 0.5

  /** Maximum zoom level allowed. */
  private val maxZoom: Double = 3.0

  /**
   * Handles the mouse pressed event to record the initial mouse position.
   *
   * @param event The mouse event.
   */
  this.onMousePressed = (event: MouseEvent) =>
    initialX = event.sceneX
    initialY = event.sceneY

  /**
   * Handles the mouse dragged event to translate the game board based on mouse movement.
   *
   * @param event The mouse event.
   */
  this.onMouseDragged = (event: MouseEvent) =>
    val offsetX = event.sceneX - initialX
    val offsetY = event.sceneY - initialY

    this.translateX = this.translateX.value + offsetX
    this.translateY = this.translateY.value + offsetY

    initialX = event.sceneX
    initialY = event.sceneY

  /**
   * Handles the scroll event to zoom in and out of the game board.
   *
   * @param event The scroll event.
   */
  this.onScroll = (event: ScrollEvent) =>
    zoomFactor = if event.deltaY > 0 then
      Math.min(zoomFactor + zoomIncrement, maxZoom) // Zoom in
    else
      Math.max(zoomFactor - zoomIncrement, minZoom) // Zoom out

    if zoomFactor > 1 && zoomFactor != maxZoom then
      this.prefWidth  = this.width.value - this.width.value * zoomFactor
      this.prefHeight = this.height.value - this.height.value * zoomFactor
    else if zoomFactor < 1  && zoomFactor != minZoom then
      this.prefWidth = this.width.value + this.width.value * zoomFactor
      this.prefHeight = this.height.value + this.height.value * zoomFactor

    this.scaleX = zoomFactor
    this.scaleY = zoomFactor

    event.consume()

  // Set the minimum size of the game board to its preferred size.
  this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)

  // Set the preferred size of the game board.
  this.setPrefSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)

  // Set the maximum size of the game board to the maximum possible value.
  this.setMaxSize(Double.MaxValue, Double.MaxValue)

  // Align the game board to the center of the stack pane.
  StackPane.setAlignment(this, Pos.Center)

  this.background = new Background(Array(new BackgroundFill(SFXColor.getCustomSFXColor(180, 239, 104, 1), CornerRadii.Empty, Insets.Empty)))

  this.hgrow = Priority.Always