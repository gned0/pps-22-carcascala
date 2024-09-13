package carcassonne.view.gameMatch

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchMenu
import carcassonne.observers.subjects.view.SubjectGameMenuView
import carcassonne.util.{Logger, Position}
import carcassonne.view.gameMatch.GameMatchMenuView.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}

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

  private def rotateDrawnTile(tile: GameTile, tileImage: ImageView, clockwise: Boolean): Unit =
    val angle = if (clockwise) TileRotationAngle else -TileRotationAngle
    val newTile = if (clockwise) tile.rotateClockwise else tile.rotateCounterClockwise
    tileImage.rotate = tileImage.getRotate + angle
    setDrawnTile(newTile, tileImage)
    redrawTile(newTile, tileImage)
    rotateClockwise.onMouseClicked = _ => rotateDrawnTile(newTile, tileImage, clockwise = true)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTile(newTile, tileImage, clockwise = false)
    Logger.log("MENU VIEW", s"Drawn tile rotated ${if (clockwise) "clockwise" else "counterclockwise"}")


  private def redrawTile(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tile, tileImage)

  override def tileDrawn(tile: GameTile): Unit =
    skipFollowerPlacement.disable = true
    rotateClockwise.disable = false
    rotateCounterClockwise.disable = false
    Logger.log("MENU VIEW", "Tile drawn")
    drawnTilePane.getChildren.clear()
    val tileImage = new ImageView(new Image(getClass.getResource(s"../../../tiles/${tile.imagePath}").toExternalForm)) {
      fitWidth = TileImageSize
      fitHeight = TileImageSize
      preserveRatio = true
    }
    setDrawnTile(tile, tileImage)
    addDrawnTilePaneElements(tile, tileImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = true)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = false)

  private def addDrawnTilePaneElements(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.add(new Text(s"North Border: \n${tile.segments(TileSegment.N)}"), TileBorderCoordinates("North")._1, TileBorderCoordinates("North")._2)
    drawnTilePane.add(new Text(s"East Border: \n${tile.segments(TileSegment.E)}"), TileBorderCoordinates("East")._1, TileBorderCoordinates("East")._2)
    drawnTilePane.add(new Text(s"South Border: \n${tile.segments(TileSegment.S)}"), TileBorderCoordinates("South")._1, TileBorderCoordinates("South")._2)
    drawnTilePane.add(new Text(s"West Border: \n${tile.segments(TileSegment.W)}"), TileBorderCoordinates("West")._1, TileBorderCoordinates("West")._2)
    drawnTilePane.add(tileImage, TileBorderCoordinates("Center")._1, TileBorderCoordinates("Center")._2)

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = s"Current Player: ${player.name}"
    playerText.fill = player.getSFXColor
    followerNumber.text = s"Follower Number: ${player.getFollowers}"
    followerNumber.fill = player.getSFXColor

  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    skipFollowerPlacement.disable = false
    rotateClockwise.disable = true
    rotateCounterClockwise.disable = true
    skipFollowerPlacement.onMouseClicked = _ => notifySkipFollowerPlacement(position: Position)

}
