import scalafx.application.*
import scalafx.geometry.Insets
import scalafx.scene.Scene

object CarcassonneApp extends JFXApp3:
  override def start(): Unit =
    val model = new GameMap()
    val view = new ObserverGameMapView()
    val controller = new GameMapController(model, view)
    controller.initialize()


    stage = new JFXApp3.PrimaryStage:
      title = "Carcassonne Map"
      scene = new Scene(600, 600):
        stylesheets.add(getClass.getResource("placeholderTile.css").toExternalForm)
        println(stylesheets)
        root = view