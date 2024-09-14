package carcassonne.view.gameMatch

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchMenu
import carcassonne.observers.subjects.view.SubjectGameMenuView
import carcassonne.util.{Logger, Position}
import carcassonne.view.gameMatch.GameMatchMenuView.{BackgroundColor, DefaultFontName, FollowerFontSize, PaneSpacing, TileBorderCoordinates, TileImageSize, TileRotationAngle, TitleFontSize}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{Background, BackgroundFill, GridPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}

object GameMatchMenuView:
  val DefaultFontName = "Arial"
  val TitleFontSize = 20
  val FollowerFontSize = 15
  val PaneSpacing = 10
  val TileImageSize = 100
  val BackgroundColor: Color = Color.DarkGray
  val TileRotationAngle = 90
  val TileBorderCoordinates: Map[String, (Int, Int)] = Map(
    "North" -> (10, 10),
    "East" -> (11, 11),
    "South" -> (10, 12),
    "West" -> (9, 11),
    "Center" -> (10, 11)
  )

class GameMatchMenuView(drawnTilePane: GridPane) extends VBox
  with SubjectGameMenuView
  with ObserverGameMatchMenu:

  private val playerText: Text = new Text("Current Player: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)

  private val followerNumber: Text = new Text("Follower Number: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, FollowerFontSize)

  val rotateClockwise: Button = new Button("Clockwise"):
    alignment = Pos.TopCenter

  val rotateCounterClockwise: Button = new Button("Counter Clockwise"):
    alignment = Pos.TopCenter

  val skipFollowerPlacement: Button = new Button("Skip Follower Placement"):
    alignment = Pos.TopCenter

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
  this.background = new Background(Array(new BackgroundFill(BackgroundColor, null, null)))
  this.spacing = PaneSpacing
  this.vgrow = Always

  private def rotateDrawnTile(tile: GameTile, tileImage: ImageView, clockwise: Boolean): Unit =
    tileImage.rotate = tileImage.getRotate + (if clockwise then TileRotationAngle else -TileRotationAngle)
    setUpTile(
      if clockwise then tile.rotateClockwise else tile.rotateCounterClockwise,
      tileImage
    )
    Logger.log("MENU VIEW", s"Drawn tile rotated ${if clockwise then "clockwise" else "counterclockwise"}")

  private def redrawTile(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tile, tileImage)

  private def setUpTile(tile: GameTile, tileImage: ImageView): Unit =
    setDrawnTile(tile, tileImage)
    redrawTile(tile, tileImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = true)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = false)

  private def addDrawnTilePaneElements(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.add(
      new Text(s"North: \n${tile.segments(TileSegment.N)}"), TileBorderCoordinates("North")._1, TileBorderCoordinates("North")._2)
    drawnTilePane.add(new Text(s"East: \n${tile.segments(TileSegment.E)}"), TileBorderCoordinates("East")._1, TileBorderCoordinates("East")._2)
    drawnTilePane.add(new Text(s"South: \n${tile.segments(TileSegment.S)}"), TileBorderCoordinates("South")._1, TileBorderCoordinates("South")._2)
    drawnTilePane.add(new Text(s"West: \n${tile.segments(TileSegment.W)}"), TileBorderCoordinates("West")._1, TileBorderCoordinates("West")._2)
    drawnTilePane.add(tileImage, TileBorderCoordinates("Center")._1, TileBorderCoordinates("Center")._2)

  private def setUpButtons(activateRotation: Boolean,
                           position: Option[Position]): Unit =
    skipFollowerPlacement.onMouseClicked = _ => notifySkipTurn(position)
    rotateClockwise.disable = activateRotation
    rotateCounterClockwise.disable = activateRotation

  override def tileDrawn(tile: GameTile): Unit =
    setUpButtons(false, None)
    Logger.log("MENU VIEW", "Tile drawn")
    val tileImage = new ImageView(new Image(getClass.getResource(s"../../../tiles/${tile.imagePath}").toExternalForm)):
      fitWidth = TileImageSize
      fitHeight = TileImageSize
      preserveRatio = true
    setUpTile(tile, tileImage)

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = s"Current Player: ${player.name}"
    playerText.fill = player.getSFXColor
    followerNumber.text = s"Follower Number: ${player.getFollowers}"
    followerNumber.fill = player.getSFXColor

  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    setUpButtons(true, Some(position))