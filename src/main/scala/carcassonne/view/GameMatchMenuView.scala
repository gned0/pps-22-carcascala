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
import GameMatchMenuView.*

object GameMatchMenuView {
  val DefaultFontName = "Arial"
  val TitleFontSize = 20
  val FollowerFontSize = 15
  val PaneSpacing = 10
  val TileImageSize = 100
  val BackgroundColor = "darkgray"
  val TileRotationAngle = 90
  val TileBorderCoordinates = Map(
    "North" -> (10, 10),
    "East" -> (11, 11),
    "South" -> (10, 12),
    "West" -> (9, 11),
    "Center" -> (10, 11)
  )
}

class GameMatchMenuView(drawnTilePane: GridPane) extends VBox
  with SubjectGameMenuView
  with ObserverGameMatchMenu {

  private val playerText: Text = new Text("Current Player: "):
    fill = Color.White
    alignment = Pos.Center
    font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)

  private val followerNumber: Text = new Text("Follower Number: "):
    fill = Color.White
    alignment = Pos.Center
    font = Font.font(DefaultFontName, FollowerFontSize)

  val rotateClockwise: Button = new Button("Clockwise"):
    alignment = Pos.TopCenter

  val rotateCounterClockwise: Button = new Button("Counter Clockwise"):
    alignment = Pos.TopCenter

  val skipFollowerPlacement: Button = new Button("Skip Follower Placement"):
    alignment = Pos.TopCenter
    disable = true

  this.children = Seq(
    playerText,
    followerNumber,
    drawnTilePane,
    skipFollowerPlacement,
    new HBox:
      alignment = Pos.TopCenter
      children = Seq(
        rotateClockwise,
        rotateCounterClockwise
      )
  )
  this.alignment = Pos.TopCenter
  this.prefWidth = 250
  this.style = s"-fx-background-color: $BackgroundColor;"
  this.spacing = PaneSpacing

  private def rotateDrawnTileClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    val newTileDrawn = tileDrawn.rotateClockwise
    tileDrawnImage.rotate = tileDrawnImage.getRotate + TileRotationAngle
    setDrawnTile(newTileDrawn, tileDrawnImage)
    redrawTileDrawn(newTileDrawn, tileDrawnImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(newTileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(newTileDrawn, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated clockwise")

  private def rotateDrawnTileCounterClockwise(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    val newTileDrawn = tileDrawn.rotateCounterClockwise
    tileDrawnImage.rotate = tileDrawnImage.getRotate - TileRotationAngle
    setDrawnTile(newTileDrawn, tileDrawnImage)
    redrawTileDrawn(newTileDrawn, tileDrawnImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(newTileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(newTileDrawn, tileDrawnImage)
    Logger.log(s"MENU VIEW", "Drawn tile rotated counter-clockwise")

  private def redrawTileDrawn(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tileDrawn, tileDrawnImage)

  override def tileDrawn(tileDrawn: GameTile): Unit =
    skipFollowerPlacement.disable = true
    rotateClockwise.disable = false
    rotateCounterClockwise.disable = false
    Logger.log("MENU VIEW", "Tile drawn")
    drawnTilePane.getChildren.clear()
    val tileDrawnImage = new ImageView(new Image(getClass.getResource("../../tiles/" + tileDrawn.imagePath).toExternalForm))

    tileDrawnImage.fitWidth = TileImageSize
    tileDrawnImage.fitHeight = TileImageSize
    tileDrawnImage.preserveRatio = true
    setDrawnTile(tileDrawn, tileDrawnImage)

    addDrawnTilePaneElements(tileDrawn, tileDrawnImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTileClockwise(tileDrawn, tileDrawnImage)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTileCounterClockwise(tileDrawn, tileDrawnImage)

  private def addDrawnTilePaneElements(tileDrawn: GameTile, tileDrawnImage: ImageView): Unit =
    drawnTilePane.add(new Text(s"North Border: \n${tileDrawn.segments(TileSegment.N)}"), TileBorderCoordinates("North")._1, TileBorderCoordinates("North")._2)
    drawnTilePane.add(new Text(s"East Border: \n${tileDrawn.segments(TileSegment.E)}"), TileBorderCoordinates("East")._1, TileBorderCoordinates("East")._2)
    drawnTilePane.add(new Text(s"South Border: \n${tileDrawn.segments(TileSegment.S)}"), TileBorderCoordinates("South")._1, TileBorderCoordinates("South")._2)
    drawnTilePane.add(new Text(s"West Border: \n${tileDrawn.segments(TileSegment.W)}"), TileBorderCoordinates("West")._1, TileBorderCoordinates("West")._2)
    drawnTilePane.add(tileDrawnImage, TileBorderCoordinates("Center")._1, TileBorderCoordinates("Center")._2)

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = "Current Player: " + player.name
    playerText.fill = player.getSFXColor
    followerNumber.text = "Follower Number: " + player.getFollowers
    followerNumber.fill = player.getSFXColor

  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    skipFollowerPlacement.disable = false
    rotateClockwise.disable = true
    rotateCounterClockwise.disable = true
    skipFollowerPlacement.onMouseClicked = _ => notifySkipFollowerPlacement(position: Position)

}
