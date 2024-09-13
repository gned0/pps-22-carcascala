package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.TileSegment
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane}
import carcassonne.util.{Logger, Position}
import scalafx.Includes.*

/**
 * Represents the grid view for placing followers on a tile in the game match.
 *
 * @param availSegments List of available tile segments where followers can be placed.
 * @param drawnTileImage The image view of the drawn tile.
 * @param currentPlayer The current player placing the follower.
 * @param position The position of the tile on the board.
 * @param notifyFollowerPlacement Callback function to notify when a follower is placed.
 */
class GameMatchFollowerGridView(
                                 availSegments: List[TileSegment],
                                 drawnTileImage: ImageView,
                                 currentPlayer: Player,
                                 position: Position,
                                 notifyFollowerPlacement: (Position, TileSegment, Player) => Unit
                               ) extends StackPane:

  /**
   * The grid pane that holds the follower placement options.
   */
  private val followerGrid = new GridPane:
    hgap = 0
    vgap = 0
    padding = Insets(0)
    alignment = Pos.Center
    prefWidth = drawnTileImage.fitHeight.toDouble
    prefHeight = drawnTileImage.fitHeight.toDouble

    columnConstraints ++= Seq.fill(3)(new ColumnConstraints { percentWidth = 100 / 3.0 })
    rowConstraints ++= Seq.fill(3)(new RowConstraints { percentHeight = 100 / 3.0 })

  /**
   * Initializes the follower placement options on the grid.
   */
  availSegments.foreach { segment =>
    /**
     * The outline image view of the follower.
     */
    val followerOutline = new ImageView(new Image(getClass.getResource("../../../follower.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      preserveRatio = true

    /**
     * The filled image view of the follower.
     */
    val filledFollower = new ImageView(new Image(getClass.getResource("../../../follower_filled.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      opacity = 0.5
      visible = true
      preserveRatio = true
      onMouseEntered = _ => this.effect = currentPlayer.getPlayerColor
      onMouseExited =  _ => this.effect = null

    /**
     * Determines the grid position (x, y) based on the tile segment.
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

    /**
     * Handles the mouse click event for placing a follower.
     *
     * @param event The mouse event.
     */
    filledFollower.onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
      filledFollower.onMouseEntered = null
      filledFollower.onMouseExited = null
      filledFollower.effect = currentPlayer.getPlayerColor
      notifyFollowerPlacement(position, segment, currentPlayer)
      followerGrid.getChildren.removeIf(node =>
        GridPane.getColumnIndex(node) != x || GridPane.getRowIndex(node) != y
      )
      filledFollower.onMouseClicked = null

    /**
     * Adds the follower outline and filled follower to the grid.
     */
    followerGrid.add(
      new StackPane:
        children = Seq(followerOutline, filledFollower)
      , x, y)
  }

  this.maxWidth = 10
  this.maxHeight = 10
  this.children = Seq(drawnTileImage, followerGrid)