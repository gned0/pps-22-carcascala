import scalafx.scene.layout.Region
class GameMapController(model: GameMap, view: GameMapView) extends Observer[GameMapView]:

  def initialize(): Unit =
  // Set up the view to listen for tile click events and place a tile
  view.addObserver(this)

  def placeTile(position: Position): Unit =
  // Here, we place a tile in the model when a position is clicked in the view
    try
      val tile = GameTile.startTile // Create a new tile
      model.placeTile(tile, position)
      log(s"Placed tile at $position")
    catch
      case e: IllegalArgumentException => println(e.getMessage)

  override def receiveUpdate(subject: GameMapView): Unit =
    placeTile(subject.getLastTilePlacedPosition.get)

  override def receiveModelMapTile(position: Position, region: Region): Unit =
    view.tileClicked(position, region, model.getTileMap.get)
    

  def log(string: String): Unit =
    print(s"CONTROLLER - " + string + "\n")