import scalafx.application.*
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{Region, StackPane}
import scalafx.Includes.*

object CarcassonneApp extends JFXApp3:
  override def start(): Unit =
    val model = new GameMap()
    val view = new GameMapView()
    model.addObserver(view)
    val controller = new GameMapController(model, view)
    controller.initialize()

    // Disable resizing of GridPane within the StackPane
    view.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    view.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)

    // Variables to track the initial mouse position
    var initialX = 0.0
    var initialY = 0.0

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

    stage = new JFXApp3.PrimaryStage:
      title = "Carcassonne Map"
      scene = new Scene(600, 600):
        stylesheets.add(getClass.getResource("placeholderTile.css").toExternalForm)
        root = containerPane