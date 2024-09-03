package carcassonne.view

import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox, Region, StackPane}
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.paint.Color

class GameBoardView() extends StackPane {

  // Initial mouse position
  private var initialX = 0.0
  private var initialY = 0.0

  // Zoom level
  private var zoomFactor = 1.0
  private val zoomIncrement = 0.1 // Zoom step size
  private val minZoom = 0.5 // Minimum zoom level
  private val maxZoom = 4.0 // Maximum zoom level

  this.background = new Background(
    Array(
      new BackgroundFill(
      Color.Pink,
      CornerRadii.Empty,
      Insets.Empty
      )
    )
  )

//  this.effect = new ColorAdjust():
//    hue = 240.0
//    brightness = 44.0
//    contrast = 0.0
//    saturation = 100.0

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
  this.setPrefSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
  this.setMaxSize(Double.MaxValue, Double.MaxValue)
  StackPane.setAlignment(this, Pos.Center)
  StackPane.setMargin(this, Insets(10))
}
