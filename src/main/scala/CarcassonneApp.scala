import scalafx.application.*
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.input.{MouseEvent, ScrollEvent}
import scalafx.scene.layout.{Region, StackPane}
import scalafx.Includes.* // Include helpers to minimize verbosity

object CarcassonneApp extends JFXApp3:

  // TODO Think if this needs changing
  // Initial mouse position
  private var initialX = 0.0
  private var initialY = 0.0

  // Zoom level
  private var zoomFactor = 1.0
  private val zoomIncrement = 0.1 // Zoom step size
  private val minZoom = 0.5       // Minimum zoom level
  private val maxZoom = 4.0       // Maximum zoom level

  override def start(): Unit =
    val model = GameMap()
    val view = GameMapView()
    val controller = GameMapController(model, view)
    controller.initialize()

    view.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    view.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)

    val containerPane = StackPane()
    containerPane.getChildren.add(view)

    containerPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    containerPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
    containerPane.setMaxSize(Double.MaxValue, Double.MaxValue)

    containerPane.onMousePressed = (event: MouseEvent) =>
      initialX = event.sceneX
      initialY = event.sceneY

    containerPane.onMouseDragged = (event: MouseEvent) =>
      val offsetX = event.sceneX - initialX
      val offsetY = event.sceneY - initialY

      containerPane.translateX = containerPane.translateX.value + offsetX
      containerPane.translateY = containerPane.translateY.value + offsetY

      initialX = event.sceneX
      initialY = event.sceneY

    containerPane.onScroll = (event: ScrollEvent) =>
      zoomFactor = if event.deltaY > 0 then
        Math.min(zoomFactor + zoomIncrement, maxZoom) // Zoom in
      else
        Math.max(zoomFactor - zoomIncrement, minZoom) // Zoom out

      containerPane.scaleX = zoomFactor
      containerPane.scaleY = zoomFactor

      event.consume()

    stage = new JFXApp3.PrimaryStage:
      title = "Carcassonne Map"
      scene = new Scene(600, 600):
        stylesheets.add(getClass.getResource("placeholderTile.css").toExternalForm)
        root = containerPane
