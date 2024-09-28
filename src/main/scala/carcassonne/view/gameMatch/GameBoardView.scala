package carcassonne.view.gameMatch

import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.Image
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import carcassonne.util.Color as SFXColor
import scalafx.scene.control.Button
import scalafx.scene.transform.Scale

/**
 * Represents the game board view in the Carcassonne game.
 * This class extends `StackPane` and provides functionality for handling mouse events
 * such as dragging and zooming.
 *
 * @param centerButton The button used to center the game board.
 */
class GameBoardView(centerButton: Button) extends StackPane:

  /** Initial X position of the mouse when pressed. */
  private var initialX: Double = -115.0

  /** Initial Y position of the mouse when pressed. */
  private var initialY: Double = 0.0

  /** Current zoom factor of the game board. */
  private var zoomFactor: Double = 1.1

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

  // Set minimum size of the game board view
  this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)

  // Set preferred size of the game board view
  this.setPrefSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)

  // Set maximum size of the game board view
  this.setMaxSize(Double.MaxValue, Double.MaxValue)

  // Align the game board view to the center of the stack pane
  StackPane.setAlignment(this, Pos.Center)

  // Set the background color of the game board view
  this.background = new Background(Array(new BackgroundFill(SFXColor.getCustomSFXColor(97, 204, 246, 1), CornerRadii.Empty, Insets.Empty)))

  // Allow the game board view to grow horizontally
  this.hgrow = Priority.Always

  // Initialize the translation and scaling of the game board view
  this.translateX = this.translateX.value + initialX
  this.translateY = this.translateY.value + initialY
  this.scaleX = zoomFactor
  this.scaleY = zoomFactor

  /**
   * Handles the mouse clicked event on the center button to reset the game board view.
   */
  this.centerButton.onMouseClicked = _ =>
    initialX = -115
    initialY = 0
    zoomFactor = 1.1
    this.translateX = initialX
    this.translateY = initialY
    this.scaleX = zoomFactor
    this.scaleY = zoomFactor