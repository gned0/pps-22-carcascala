package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.TileSegment
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane}
import carcassonne.util.{Logger, Position}
import scalafx.Includes.*
import scalafx.scene.Cursor

/** Represents the grid view for placing followers on a tile in the game match.
  *
  * @param availSegments
  *   List of available tile segments where followers can be placed.
  * @param drawnTileImage
  *   The image view of the drawn tile.
  * @param currentPlayer
  *   The current player placing the follower.
  * @param position
  *   The position of the tile on the board.
  * @param notifyFollowerPlacement
  *   Callback function to notify when a follower is placed.
  */
class GameMatchFollowerGridView(
    availSegments: List[TileSegment],
    drawnTileImage: ImageView,
    currentPlayer: Player,
    position: Position,
    notifyFollowerPlacement: (Position, TileSegment, Player) => Unit,
    createNewPlaceholders: Position => Unit
) extends StackPane:

  /** The grid pane that holds the follower placement options. It is a 3x3 grid where each cell can potentially hold a follower.
    */
  private val followerGrid = new GridPane:
    hgap = 0
    vgap = 0
    padding = Insets(0)
    alignment = Pos.Center
    prefWidth = drawnTileImage.fitWidth.toDouble
    prefHeight = drawnTileImage.fitHeight.toDouble

    // Define column and row constraints to divide the grid into 3 equal parts
    columnConstraints ++= Seq.fill(3)(ColumnConstraints(drawnTileImage.fitWidth.toDouble / 3.2))
    rowConstraints ++= Seq.fill(3)(RowConstraints(drawnTileImage.fitHeight.toDouble / 3.2))

  /** Initializes the follower placement options on the grid. For each available segment, it creates an outline and a filled follower image.
    */
  availSegments.foreach(segment =>
    /** The outline image view of the follower. This is displayed as a placeholder where a follower can be placed.
      */
    val followerOutline = new ImageView(Image("follower.png")):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      preserveRatio = true

    /** The filled image view of the follower. This is displayed when a follower is placed on the tile.
      */
    val filledFollower = new ImageView(Image("follower_filled.png")):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      opacity = 0.5
      visible = true
      preserveRatio = true
      onMouseEntered = _ =>
        this.effect = currentPlayer.getPlayerColor
        this.cursor = Cursor.Hand
      onMouseExited = _ =>
        this.effect = null
        this.cursor = Cursor.Default

    /** Determines the grid position (x, y) based on the tile segment. Each segment corresponds to a specific position on the grid.
      */
    val (x, y) = segment match
      case TileSegment.N  => (1, 0)
      case TileSegment.E  => (2, 1)
      case TileSegment.S  => (1, 2)
      case TileSegment.W  => (0, 1)
      case TileSegment.NE => (2, 0)
      case TileSegment.NW => (0, 0)
      case TileSegment.SE => (2, 2)
      case TileSegment.SW => (0, 2)
      case _              => (1, 1)

    /** Handles the mouse click event for placing a follower. When a follower is placed, it notifies the game logic and updates the UI.
      *
      * @param event
      *   The mouse event.
      */
    filledFollower.onMouseClicked = (event: MouseEvent) =>
      if event.button == MouseButton.Primary then
        filledFollower.onMouseEntered = null
        filledFollower.onMouseExited = null
        filledFollower.effect = currentPlayer.getPlayerColor
        notifyFollowerPlacement(position, segment, currentPlayer)
        createNewPlaceholders(position)
        followerGrid.getChildren.removeIf(node => GridPane.getColumnIndex(node) != x || GridPane.getRowIndex(node) != y)
        filledFollower.onMouseClicked = null

    /** Adds the follower outline and filled follower to the grid. Each cell in the grid can contain a StackPane with both images.
      */
    followerGrid.add(
      new StackPane:
        children = Seq(followerOutline, filledFollower)
      ,
      x,
      y
    )
  )

  this.minWidth = 100
  this.minHeight = 100

  // Add the drawn tile image and the follower grid to the StackPane
  this.children = Seq(drawnTileImage, followerGrid)
