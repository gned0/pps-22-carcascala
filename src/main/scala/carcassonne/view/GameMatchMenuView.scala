package carcassonne.view

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.ObserverGameMatchMenu
import carcassonne.observers.subjects.view.SubjectGameMenuView
import carcassonne.util.{Logger, Position}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}

class GameMatchMenuView(drawnTilePane: GridPane) extends VBox
  with SubjectGameMenuView
  with ObserverGameMatchMenu {

  private val playerText: Text = new Text("Current Player: "):
    fill = Color.White
    alignment = Pos.Center
    font = Font.font("Arial", FontWeight.Bold, 20)
  private val meepleNumber: Text = new Text("Meeple Number: "):
    fill = Color.White
    alignment = Pos.Center
    font = Font.font("Arial", 15)
  val rotateClockwise: Button = new Button("Clockwise"):
    alignment = Pos.TopCenter
  val rotateCounterClockwise: Button = new Button("Counter Clockwise"):
    alignment = Pos.TopCenter

  this.children = Seq(
    playerText,
    meepleNumber,
    drawnTilePane,
    new HBox:
      alignment = Pos.TopCenter
      children = Seq(
        rotateClockwise,
        rotateCounterClockwise
      )
  )
  this.alignment = Pos.TopCenter
  this.prefWidth = 250
  this.style = "-fx-background-color: darkgray;"
  this.spacing = 10

  private def rotateDrawnTileClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    val newTileDrawn = tileDrawn.rotateClockwise
    tileDrawnImage.rotate = tileDrawnImage.getRotate + 90
    setDrawnTile(newTileDrawn, tileDrawnImage)
    redrawTileDrawn(newTileDrawn, tileDrawnImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(newTileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(newTileDrawn, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated clockwise")

  private def rotateDrawnTileCounterClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    val newTileDrawn = tileDrawn.rotateCounterClockwise
    tileDrawnImage.rotate = tileDrawnImage.getRotate - 90
    setDrawnTile(newTileDrawn, tileDrawnImage)
    redrawTileDrawn(newTileDrawn, tileDrawnImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(newTileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(newTileDrawn, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated ch3 clockwise")


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
    setDrawnTile(tileDrawn, tileDrawnImage)

    addDrawnTilePaneElements(tileDrawn, tileDrawnImage)
    
    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(tileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(tileDrawn, tileDrawnImage)


  private def addDrawnTilePaneElements(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    drawnTilePane.add(new Text(s"North Border: \n${tileDrawn.segments(TileSegment.N)}"), 10, 10)
    drawnTilePane.add(new Text(s"East Border: \n${tileDrawn.segments(TileSegment.E)}"), 11, 11)
    drawnTilePane.add(new Text(s"South Border: \n${tileDrawn.segments(TileSegment.S)}"), 10, 12)
    drawnTilePane.add(new Text(s"West Border: \n${tileDrawn.segments(TileSegment.W)}"), 9, 11)
    drawnTilePane.add(tileDrawnImage, 10, 11)

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = "Current Player: " + player.name
    playerText.fill = player.getSFXColor
    meepleNumber.text = "Meeple Number: " + player.getFollowers
    meepleNumber.fill = player.getSFXColor

}
