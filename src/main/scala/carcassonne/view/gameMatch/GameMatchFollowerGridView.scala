package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.TileSegment
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane}
import carcassonne.util.{Logger, Position}
import scalafx.Includes.*

class GameMatchFollowerGridView(
                                 availSegments: List[TileSegment],
                                 drawnTileImage: ImageView,
                                 currentPlayer: Player,
                                 position: Position,
                                 notifyFollowerPlacement: (Position, TileSegment, Player) => Unit
                               ) extends StackPane:

  private val followerGrid = new GridPane:
    hgap = 0
    vgap = 0
    padding = Insets(0)
    alignment = Pos.Center
    prefWidth = drawnTileImage.fitHeight.toDouble
    prefHeight = drawnTileImage.fitHeight.toDouble

    columnConstraints ++= Seq.fill(3)(new ColumnConstraints { percentWidth = 100 / 3.0 })
    rowConstraints ++= Seq.fill(3)(new RowConstraints { percentHeight = 100 / 3.0 })

  availSegments.foreach { segment =>
    val followerOutline = new ImageView(new Image(getClass.getResource("../../../follower.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      preserveRatio = true

    val filledFollower = new ImageView(new Image(getClass.getResource("../../../follower_filled.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      opacity = 0.5
      visible = true
      preserveRatio = true
      onMouseEntered = _ => this.effect = currentPlayer.getPlayerColor
      onMouseExited =  _ => this.effect = null

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

    filledFollower.onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
      filledFollower.onMouseEntered = null
      filledFollower.onMouseExited = null
      filledFollower.effect = currentPlayer.getPlayerColor
      notifyFollowerPlacement(position, segment, currentPlayer)
      followerGrid.getChildren.removeIf(node =>
        GridPane.getColumnIndex(node) != x || GridPane.getRowIndex(node) != y
      )
      filledFollower.onMouseClicked = null

    followerGrid.add(
      new StackPane:
        children = Seq(followerOutline, filledFollower)
      , x, y)
  }

  this.maxWidth = 10
  this.maxHeight = 10
  this.children = Seq(drawnTileImage, followerGrid)