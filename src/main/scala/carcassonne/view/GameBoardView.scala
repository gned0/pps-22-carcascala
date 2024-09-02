package carcassonne.view

import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.{Region, StackPane}
import scalafx.Includes.*

class GameBoardView extends StackPane{

  // Initial mouse position
  private var initialX = 0.0
  private var initialY = 0.0

  // Zoom level
  private var zoomFactor = 1.0
  private val zoomIncrement = 0.1 // Zoom step size
  private val minZoom = 0.5 // Minimum zoom level
  private val maxZoom = 4.0 // Maximum zoom level

  this.onMousePressed = (event: MouseEvent) =>
    initialX = event.sceneX
    initialY = event.sceneY

  this.onMouseDragged = (event: MouseEvent) =>
    val offsetX = event.sceneX - initialX
    val offsetY = event.sceneY - initialY

    this.translateX = this.translateX.value + offsetX
    this.translateY = this.translateY.value + offsetY

    initialX = event.sceneX
    initialY = event.sceneY

  this.onScroll = (event: ScrollEvent) =>
    zoomFactor = if event.deltaY > 0 then
      Math.min(zoomFactor + zoomIncrement, maxZoom) // Zoom in
    else
      Math.max(zoomFactor - zoomIncrement, minZoom) // Zoom out

    this.scaleX = zoomFactor
    this.scaleY = zoomFactor

    event.consume()

  this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
  this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
  this.setMaxSize(Double.MaxValue, Double.MaxValue)
  
}
