package carcassonne.view.gameMatch

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.model.ObserverGameMatchMenu
import carcassonne.observers.subjects.view.SubjectGameMenuView
import carcassonne.util.{Logger, Position}
import carcassonne.view.gameMatch.GameMatchMenuView.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Cursor
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.{Always, Never}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import carcassonne.util.Color as CarcaColor
import scalafx.scene.text.{Font, FontWeight, Text}

object GameMatchMenuView:
  /** Default font name used in the view */
  val DefaultFontName = "Arial"

  /** Font size for the title text */
  val TitleFontSize = 20

  /** Font size for the follower number text */
  val FollowerFontSize = 18

  /** Spacing between panes in the view */
  val PaneSpacing = 10

  /** Size of the tile images */
  val TileImageSize = 100

  /** Background color of the view */
  val BackgroundColor: Color = Color.DarkGray

  /** Rotation angle for the tiles */
  val TileRotationAngle = 90

  /** Default background color for tile rotation buttons */
  val defaultRotateButtonsBackground = Background(
    Array(BackgroundFill(CarcaColor.getCustomSFXColor(220, 220, 220, 1), CornerRadii(10), Insets.Empty))
  )

  /** Background color for disabled tile rotation buttons */
  val disabledRotateButtonsBackground = Background(
    Array(BackgroundFill(CarcaColor.getCustomSFXColor(100, 100, 100, 1), CornerRadii(10), Insets.Empty))
  )

  /** Coordinates for the tile borders */
  val TileBorderCoordinates: Map[String, (Int, Int)] = Map(
    "North" -> (10, 10),
    "East" -> (11, 11),
    "South" -> (10, 12),
    "West" -> (9, 11),
    "Center" -> (10, 11)
  )

/** Represents the game match menu view in the Carcassonne game.
 *
 * @param drawnTilePane
 *   The pane where the drawn tile is displayed.
 */
class GameMatchMenuView(drawnTilePane: GridPane) extends VBox with SubjectGameMenuView with ObserverGameMatchMenu:

  private val playerText: Text = new Text("Current Player: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)

  private val followerNumber: Text = new Text("Follower Number: "):
    fill = Color.White
    alignment = Pos.TopCenter
    font = Font.font(DefaultFontName, FontWeight.Bold, FollowerFontSize)

  private val scoreboard: VBox = new VBox():
    alignment = Pos.TopCenter
    padding = Insets(5, 0, 15, 0)
    spacing = 5
    border = Border(
      BorderStroke(
        Color.Black,
        BorderStrokeStyle.Solid,
        CornerRadii.Empty,
        BorderWidths.Default
      )
    )
  children = Seq(new Text("Scoreboard:"):
    fill = Color.White
    font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)
  )

  private val remainingTiles: VBox = new VBox():
    alignment = Pos.TopCenter
    padding = Insets(5, 0, 15, 0)
    spacing = 5
    children = Seq(new Text("Remaining tiles:"):
      fill = Color.White
      font = Font.font(DefaultFontName, FontWeight.Bold, FollowerFontSize)
    )

  val rotateClockwise: StackPane = createRotateButtons("rotateClockwise.png")
  val rotateCounterClockwise: StackPane = createRotateButtons("rotateCounterClockwise.png")
  val skipFollowerPlacement: Button = new Button("Skip Placement"):
    alignment = Pos.TopCenter

  val endGameButton: Button = new Button("End Game"):
    alignment = Pos.Center
    padding = Insets(5)
    font = Font("Arial", 18)
    onMouseClicked = _ => notifyEndGameEarly()

  this.children = Seq(
    new VBox():
      alignment = Pos.TopCenter
      padding = Insets(15, 5, 15, 5)
      border = Border(
        BorderStroke(
          Color.Black,
          BorderStrokeStyle.Solid,
          CornerRadii.Empty,
          BorderWidths.Default
        )
      )
      children = Seq(playerText, followerNumber)
    ,
    new VBox():
      alignment = Pos.TopCenter
      padding = Insets(15, 5, 15, 5)
      border = Border(
        BorderStroke(
          Color.Black,
          BorderStrokeStyle.Solid,
          CornerRadii.Empty,
          BorderWidths.Default
        )
      )
      children = Seq(
        remainingTiles,
        drawnTilePane,
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
    ,
    scoreboard,
    endGameButton
  )
  this.alignment = Pos.TopCenter
  this.minWidth = 250
  this.maxWidth = 250
  this.background = Background(Array(BackgroundFill(BackgroundColor, CornerRadii.Empty, Insets.Empty)))
  this.border = Border(BorderStroke(Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(3)))
  this.spacing = PaneSpacing
  this.vgrow = Always
  this.hgrow = Never
  this.padding = Insets(10)

  /** Creates the Stackpane for the rotate tiles buttons
   *
   * @param imagePath
   *   The image to show on screen
   */
  private def createRotateButtons(imagePath: String): StackPane =
    new StackPane():
      prefWidth = TileImageSize * 0.50
      prefHeight = TileImageSize * 0.50
      children = Seq(
        new Region():
          prefWidth = TileImageSize * 0.50
          prefHeight = TileImageSize * 0.50
        ,
        new ImageView(Image(imagePath)):
          alignment = Pos.Center
          fitWidth = TileImageSize * 0.40
          fitHeight = TileImageSize * 0.40
          preserveRatio = true
      )
      background = defaultRotateButtonsBackground
      onMouseEntered = _ =>
        this.background = Background(Array(BackgroundFill(Color.White, CornerRadii(10), Insets.Empty)))
        this.cursor = Cursor.Hand
      onMouseExited = _ =>
        this.background = defaultRotateButtonsBackground
        this.cursor = Cursor.Default

  /** Rotates the drawn tile.
   *
   * @param tile
   *   The tile to be rotated.
   * @param tileImage
   *   The image view of the tile.
   * @param clockwise
   *   Whether to rotate clockwise.
   */
  private def rotateDrawnTile(tile: GameTile, tileImage: ImageView, clockwise: Boolean): Unit =
    tileImage.rotate = tileImage.getRotate + (if clockwise then TileRotationAngle else -TileRotationAngle)
    setUpTile(
      if clockwise then tile.rotateClockwise else tile.rotateCounterClockwise,
      tileImage
    )
    Logger.log("MENU VIEW", s"Drawn tile rotated ${if clockwise then "clockwise" else "counterclockwise"}")

  /** Redraws the tile on the pane.
   *
   * @param tile
   *   The tile to be redrawn.
   * @param tileImage
   *   The image view of the tile.
   */
  private def redrawTile(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.getChildren.clear()
    addDrawnTilePaneElements(tile, tileImage)

  /** Sets up the tile and its image view.
   *
   * @param tile
   *   The tile to be set up.
   * @param tileImage
   *   The image view of the tile.
   */
  private def setUpTile(tile: GameTile, tileImage: ImageView): Unit =
    setDrawnTile(tile, tileImage)
    redrawTile(tile, tileImage)

    rotateClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = true)
    rotateCounterClockwise.onMouseClicked = _ => rotateDrawnTile(tile, tileImage, clockwise = false)

  /** Adds elements to the drawn tile pane.
   *
   * @param tile
   *   The tile to be added.
   * @param tileImage
   *   The image view of the tile.
   */
  private def addDrawnTilePaneElements(tile: GameTile, tileImage: ImageView): Unit =
    drawnTilePane.add(
      Text(s"North: \n${tile.segments(TileSegment.N)}"),
      TileBorderCoordinates("North")._1,
      TileBorderCoordinates("North")._2
    )
    drawnTilePane.add(
      Text(s"East: \n${tile.segments(TileSegment.E)}"),
      TileBorderCoordinates("East")._1,
      TileBorderCoordinates("East")._2
    )
    drawnTilePane.add(
      Text(s"South: \n${tile.segments(TileSegment.S)}"),
      TileBorderCoordinates("South")._1,
      TileBorderCoordinates("South")._2
    )
    drawnTilePane.add(
      Text(s"West: \n${tile.segments(TileSegment.W)}"),
      TileBorderCoordinates("West")._1,
      TileBorderCoordinates("West")._2
    )
    drawnTilePane.add(tileImage, TileBorderCoordinates("Center")._1, TileBorderCoordinates("Center")._2)

  /** Sets up the buttons for rotation and skipping follower placement.
   *
   * @param activateRotation
   *   Whether to activate rotation buttons.
   * @param position
   *   The position of the tile.
   */
  private def setUpButtons(activateRotation: Boolean, position: Option[Position]): Unit =
    skipFollowerPlacement.onMouseClicked = _ => notifySkipTurn(position)
    if activateRotation then
      rotateClockwise.background = disabledRotateButtonsBackground
      rotateCounterClockwise.background = disabledRotateButtonsBackground
    else
      rotateClockwise.background = defaultRotateButtonsBackground
      rotateCounterClockwise.background = defaultRotateButtonsBackground
    rotateClockwise.disable = activateRotation
    rotateCounterClockwise.disable = activateRotation

  /** Handles the event when a tile is drawn.
   *
   * @param tile
   *   The drawn tile.
   */
  override def tileDrawn(tile: GameTile, tilesCount: Int): Unit =
    setUpButtons(false, None)
    Logger.log("MENU VIEW", "Tile drawn")
    val tileImage = new ImageView(Image(s"tiles/${tile.imagePath}")):
      fitWidth = TileImageSize
      fitHeight = TileImageSize
      preserveRatio = true
    setUpTile(tile, tileImage)
    remainingTiles.children.clear()
    remainingTiles.children.add(new Text(s"Remaining tiles: $tilesCount"):
      fill = Color.White
      font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)
    )

  /** Handles the event when the player changes.
   *
   * @param player
   *   The new current player.
   */
  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)
    playerText.text = s"Current Player: ${player.name}"
    playerText.fill = player.getSFXColor
    followerNumber.text = s"Follower Number: ${player.getFollowers}"
    followerNumber.fill = player.getSFXColor

  /** Handles the event when available follower positions are updated.
   *
   * @param availSegments
   *   The available segments for follower placement.
   * @param position
   *   The position of the tile.
   */
  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    setUpButtons(true, Some(position))

  override def updateScoreboard(scores: Map[Player, Int]): Unit =
    scoreboard.children.clear()
    scoreboard.children.add(new Text("Scoreboard:"):
      fill = Color.White
      font = Font.font(DefaultFontName, FontWeight.Bold, TitleFontSize)
    )
    scores.foreach { (player, score) =>
      scoreboard.children.add(new Text(s"${player.name}: $score"):
        fill = player.getSFXColor
        font = Font.font(DefaultFontName, FontWeight.Normal, FollowerFontSize)
      )
    }