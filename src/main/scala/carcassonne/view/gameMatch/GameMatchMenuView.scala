package carcassonne.view.gameMatch

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchMenu
import carcassonne.observers.subjects.view.SubjectGameMenuView
import carcassonne.util.{Logger, Position}
import carcassonne.view.gameMatch.GameMatchMenuView.{BackgroundColor, DefaultFontName, FollowerFontSize, PaneSpacing, TileBorderCoordinates, TileImageSize, TileRotationAngle, TitleFontSize}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Cursor
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{Background, BackgroundFill, Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, GridPane, HBox, Region, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, Text}

object GameMatchMenuView:
  /** Default font name used in the view */
  val DefaultFontName = "Arial"

  /** Font size for the title text */
  val TitleFontSize = 20

  /** Font size for the follower number text */
  val FollowerFontSize = 15

  /** Spacing between panes in the view */
  val PaneSpacing = 10

  /** Size of the tile images */
  val TileImageSize = 100

  /** Background color of the view */
  val BackgroundColor: Color = Color.DarkGray

  /** Rotation angle for the tiles */
  val TileRotationAngle = 90

  /** Coordinates for the tile borders */
  val TileBorderCoordinates: Map[String, (Int, Int)] = Map(
    "North" -> (10, 10),
    "East" -> (11, 11),
    "South" -> (10, 12),
    "West" -> (9, 11),
    "Center" -> (10, 11)
  )

/**
 * Represents the game match menu view in the Carcassonne game.
 *
 * @param drawnTilePane The pane where the drawn tile is displayed.
 */
class GameMatchMenuView(drawnTilePane: GridPane) extends VBox
  with SubjectGameMenuView
  with ObserverGameMatchMenu:

  /** Text element displaying the current player */
  private val playerText: Text = new Text("Current Player: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)

  /** Text element displaying the follower number */
  private val followerNumber: Text = new Text("Follower Number: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, FollowerFontSize)

  /** Button to rotate the tile clockwise */
  val rotateClockwise: StackPane = new StackPane():
    prefWidth = TileImageSize * 0.50
    prefHeight = TileImageSize * 0.50
    children =
      Seq(
        new Region():
          prefWidth = TileImageSize * 0.50
          prefHeight = TileImageSize * 0.50
        ,
        new ImageView(new Image(getClass.getResource(s"../../../rotateClockwise.png").toExternalForm)):
          alignment = Pos.Center
          fitWidth = TileImageSize * 0.40
          fitHeight = TileImageSize * 0.40
          preserveRatio = true
      )
    onMouseEntered = _ =>
      this.background = new Background(Array(new BackgroundFill(Color.White, new CornerRadii(10), Insets.Empty)))
      this.cursor = Cursor.Hand
    onMouseExited = _ =>
      this.background = Background.Empty
      this.cursor = Cursor.Default

  /** Button to rotate the tile counterclockwise */
  val rotateCounterClockwise: StackPane = new StackPane():
    prefWidth = TileImageSize * 0.50
    prefHeight = TileImageSize * 0.50
    children =
      Seq(
        new Region():
          prefWidth = TileImageSize * 0.50
          prefHeight = TileImageSize * 0.50
        ,
        new ImageView(new Image(getClass.getResource(s"../../../rotateCounterClockwise.png").toExternalForm)):
          alignment = Pos.Center
          fitWidth = TileImageSize * 0.40
          fitHeight = TileImageSize * 0.40
          preserveRatio = true
      )
    onMouseEntered = _ =>
      this.background = new Background(Array(new BackgroundFill(Color.White, new CornerRadii(10), Insets.Empty)))
      this.cursor = Cursor.Hand
    onMouseExited = _ =>
      this.background = Background.Empty
      this.cursor = Cursor.Default

  /** Button to skip follower placement */
  val skipFollowerPlacement: Button = new Button("Skip Follower Placement"):
    alignment = Pos.TopCenter

  this.children = Seq(
    new VBox():
      alignment = Pos.TopCenter
      this.padding = Insets(15, 5, 15, 5)
      border = new Border(
        new BorderStroke(
          Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths.Default
        )
      )
      children =
        Seq(playerText, followerNumber)
    ,
    new VBox():
      alignment = Pos.TopCenter
      this.padding = Insets(15, 5, 15, 5)
      border = new Border(
        new BorderStroke(
          Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths.Default
        )
      )
      children =
        Seq(drawnTilePane,
          new HBox:
            alignment = Pos.TopCenter
            padding = Insets(10, 0, 10, 0)
            spacing = 30
            children = Seq(
            rotateCounterClockwise,
            rotateClockwise
          )
          ,
          skipFollowerPlacement
        )
  )
  this.alignment = Pos.TopCenter
  this.prefWidth = 250
  this.background = new Background(Array(new BackgroundFill(BackgroundColor, CornerRadii.Empty, Insets.Empty)))
  this.spacing = PaneSpacing
  this.vgrow = Always
  this.padding = Insets(10)

  /**
   * Rotates the drawn tile.
   *
   * @param tile The tile to be rotated.
   * @param tileImage The image view of the tile.
   * @param clockwise Whether to rotate clockwise.
   */
  private def rotateDrawnTile(tile: GameTile, tileImage: ImageView, clockwise: Boolean): Unit =
    tileImage.rotate = tileImage.getRotate + (if clockwise then TileRotationAngle else -TileRotationAngle)
    setUpTile(
      if clockwise then tile.rotateClockwise else tile.rotateCounterClockwise,
      tileImage
    )
    Logger.log("MENU VIEW", s"Drawn tile rotated ${if clockwise then "clockwise" else "counterclockwise"}")

  /**
   * Redraws the tile on the pane.
   *
   * @param tile The tile to be redrawn.
   * @param tileImage The image view of the tile.
   */
  private def redrawTile(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tile, tileImage)

  /**
   * Sets up the tile and its image view.
   *
   * @param tile The tile to be set up.
   * @param tileImage The image view of the tile.
   */
  private def setUpTile(tile: GameTile, tileImage: ImageView): Unit =
    setDrawnTile(tile, tileImage)
    redrawTile(tile, tileImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = true)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = false)

  /**
   * Adds elements to the drawn tile pane.
   *
   * @param tile The tile to be added.
   * @param tileImage The image view of the tile.
   */
  private def addDrawnTilePaneElements(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.add(new Text(s"North: \n${tile.segments(TileSegment.N)}"), TileBorderCoordinates("North")._1, TileBorderCoordinates("North")._2)
    drawnTilePane.add(new Text(s"East: \n${tile.segments(TileSegment.E)}"), TileBorderCoordinates("East")._1, TileBorderCoordinates("East")._2)
    drawnTilePane.add(new Text(s"South: \n${tile.segments(TileSegment.S)}"), TileBorderCoordinates("South")._1, TileBorderCoordinates("South")._2)
    drawnTilePane.add(new Text(s"West: \n${tile.segments(TileSegment.W)}"), TileBorderCoordinates("West")._1, TileBorderCoordinates("West")._2)
    drawnTilePane.add(tileImage, TileBorderCoordinates("Center")._1, TileBorderCoordinates("Center")._2)

  /**
   * Sets up the buttons for rotation and skipping follower placement.
   *
   * @param activateRotation Whether to activate rotation buttons.
   * @param position The position of the tile.
   */
  private def setUpButtons(activateRotation: Boolean,
                           position: Option[Position]): Unit =
    skipFollowerPlacement.onMouseClicked = _ => notifySkipTurn(position)
    rotateClockwise.disable = activateRotation
    rotateCounterClockwise.disable = activateRotation

  /**
   * Handles the event when a tile is drawn.
   *
   * @param tile The drawn tile.
   */
  override def tileDrawn(tile: GameTile): Unit =
    setUpButtons(false, None)
    Logger.log("MENU VIEW", "Tile drawn")
    val tileImage = new ImageView(new Image(getClass.getResource(s"../../../tiles/${tile.imagePath}").toExternalForm)):
      fitWidth = TileImageSize
      fitHeight = TileImageSize
      preserveRatio = true
    setUpTile(tile, tileImage)

  /**
   * Handles the event when the player changes.
   *
   * @param player The new current player.
   */
  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = s"Current Player: ${player.name}"
    playerText.fill = player.getSFXColor
    followerNumber.text = s"Follower Number: ${player.getFollowers}"
    followerNumber.fill = player.getSFXColor

  /**
   * Handles the event when available follower positions are updated.
   *
   * @param availSegments The available segments for follower placement.
   * @param position The position of the tile.
   */
  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    setUpButtons(true, Some(position))