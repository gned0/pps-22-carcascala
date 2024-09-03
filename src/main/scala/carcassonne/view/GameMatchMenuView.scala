package carcassonne.view

import carcassonne.model.game.{GameMatch, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.ObserverGameMatch
import carcassonne.util.{Logger, Position}
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.text.Text

class GameMatchMenuView(drawnTilePane: GridPane) extends VBox
  with ObserverGameMatch[GameMatch] {

  this.prefWidth = 250
  this.style = "-fx-background-color: darkgray;"
  this.alignment = Pos.TopLeft

  val rotateClockwise = new Button("Clockwise")
  val rotateCounterClockwise = new Button("Counter Clockwise")

  this.children = Seq(
    drawnTilePane,
    new HBox {
      children = Seq(
        rotateClockwise,
        rotateCounterClockwise
      )
    }
  )

  private def rotateDrawnTileClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    tileDrawnImage.rotate = tileDrawnImage.getRotate + 90
    redrawTileDrawn(tileDrawn.rotateClockwise, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated clockwise")

  private def rotateDrawnTileCounterClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    tileDrawnImage.rotate = tileDrawnImage.getRotate - 90
    redrawTileDrawn(tileDrawn.rotateCounterClockwise, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated counter clockwise")


  private def redrawTileDrawn(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tileDrawn, tileDrawnImage)

  override def tileDrawn(tileDrawn: GameTile): Unit =
    Logger.log("MENU VIEW", "Tile drawn")
    drawnTilePane.getChildren.clear()
    val tileDrawnImage = new ImageView(new Image(getClass.getResource("../../tiles/" + tileDrawn.imagePath).toExternalForm))

    tileDrawnImage.fitWidth = 100
    tileDrawnImage.fitHeight = 100
    tileDrawnImage.preserveRatio = true

    addDrawnTilePaneElements(tileDrawn, tileDrawnImage)
    
    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(tileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(tileDrawn, tileDrawnImage)

  override def gameEnded(players: List[Player]): Unit =
    println("nothing")

  override def isTilePlaced(isTilePlaced: Boolean, tiles: Option[Map[Position, GameTile]], position: Position): Unit =
    println("nothing")

  def addDrawnTilePaneElements(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    drawnTilePane.add(new Text(s"North Border: \n${tileDrawn.segments(TileSegment.N)}"), 10, 10)
    drawnTilePane.add(new Text(s"East Border: \n${tileDrawn.segments(TileSegment.E)}"), 11, 11)
    drawnTilePane.add(new Text(s"South Border: \n${tileDrawn.segments(TileSegment.S)}"), 10, 12)
    drawnTilePane.add(new Text(s"West Border: \n${tileDrawn.segments(TileSegment.W)}"), 9, 11)
    drawnTilePane.add(tileDrawnImage, 10, 11)
  
}
