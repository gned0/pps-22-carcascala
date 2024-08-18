import scalafx.application.*
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.{Region, StackPane}
import scalafx.Includes.*

object CarcassonneApp extends JFXApp3:

  // Variables to track the initial mouse position
  var initialX = 0.0
  var initialY = 0.0

  // Variable to track the zoom level
  var zoomFactor = 1.0
  val zoomIncrement = 0.1 // Zoom step size
  val minZoom = 0.5 // Minimum zoom level
  val maxZoom = 4.0 // Maximum zoom level

  override def start(): Unit =
    val model = new GameMap()
    val view = new GameMapView()
    model.addObserver(view)
    val controller = new GameMapController(model, view)
    controller.initialize()

    // Disable resizing of GridPane within the StackPane
    view.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    view.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)

    // Wrap the view inside a StackPane for translation (dragging)
    val containerPane = new StackPane()
    containerPane.getChildren.add(view) // Explicitly add the view to the StackPane

    // Allow StackPane to expand as GridPane grows
    containerPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    containerPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
    containerPane.setMaxSize(Double.MaxValue, Double.MaxValue)

    // Event handler for pressing the mouse
    containerPane.onMousePressed = (event: MouseEvent) => {
      initialX = event.sceneX
      initialY = event.sceneY
    }

    // Event handler for dragging the mouse
    containerPane.onMouseDragged = (event: MouseEvent) => {
      val offsetX = event.sceneX - initialX
      val offsetY = event.sceneY - initialY

      // Apply the translation to the StackPane
      containerPane.translateX = containerPane.translateX.value + offsetX
      containerPane.translateY = containerPane.translateY.value + offsetY

      // Update the initial position for the next drag event
      initialX = event.sceneX
      initialY = event.sceneY
    }

    // Event handler for mouse scroll to zoom in and out
    containerPane.onScroll = (event: ScrollEvent) => {
      // Zoom in or out depending on scroll direction
      val delta = event.deltaY
      if delta > 0 then
        zoomFactor = Math.min(zoomFactor + zoomIncrement, maxZoom) // Zoom in
      else
        zoomFactor = Math.max(zoomFactor - zoomIncrement, minZoom) // Zoom out

      // Apply the zoom (scaling) to the StackPane
      containerPane.scaleX = zoomFactor
      containerPane.scaleY = zoomFactor

      // Prevent the event from propagating further
      event.consume()
    }

    stage = new JFXApp3.PrimaryStage:
      title = "Carcassonne Map"
      scene = new Scene(600, 600):
        stylesheets.add(getClass.getResource("placeholderTile.css").toExternalForm)
        root = containerPane