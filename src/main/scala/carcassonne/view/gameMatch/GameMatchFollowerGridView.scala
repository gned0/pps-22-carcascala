package carcassonne.view.gameMatch

import carcassonne.model.game.Player
import carcassonne.model.tile.TileSegment
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane}
import carcassonne.util.{Logger, Position}
import scalafx.Includes.*

class GameMatchFollowerGridView(availSegments: List[TileSegment],
                                drawnTileImage: ImageView,
                                currentPlayer: Player,
                                position: Position,
                               notifyFollowerPlacement: (Position, TileSegment, Player) => Unit)
  extends StackPane:

  // Replace the tile that has been just removed with new attributes
  val followerGrid = new GridPane():
    hgap = 0
    vgap = 0
    padding = Insets(0)
    alignment = Pos.Center

    // Creating 3x3 grid structure with equal cell sizes
    columnConstraints ++= Seq(
      new ColumnConstraints {
        percentWidth = 100 / 3.0
      },
      new ColumnConstraints {
        percentWidth = 100 / 3.0
      },
      new ColumnConstraints {
        percentWidth = 100 / 3.0
      }
    )

    rowConstraints ++= Seq(
      new RowConstraints {
        percentHeight = 100 / 3.0
      },
      new RowConstraints {
        percentHeight = 100 / 3.0
      },
      new RowConstraints {
        percentHeight = 100 / 3.0
      }
    )

  followerGrid.prefHeight <== drawnTileImage.fitHeight.toDouble
  followerGrid.prefWidth <== drawnTileImage.fitWidth.toDouble

  availSegments.foreach(segment =>
    val followerImageView = new ImageView(new Image(getClass.getResource("../../../follower.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      preserveRatio = true

    // Create the overlay meeple with the desired fill color
    val filledFollower = new ImageView(new Image(getClass.getResource("../../../follower_filled.png").toExternalForm)):
      fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
      fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
      opacity = 0.5 // Set the opacity to make it semi-transparent
      visible = true // Initially not visible
      preserveRatio = true


    // Create a StackPane to hold the ImageView and Rectangle
    val stackPane = new StackPane {
      children = Seq(followerImageView, filledFollower)
    }

    // Add hover effect to show/hide the rectangle
    filledFollower.onMouseEntered = _ =>
      filledFollower.effect = currentPlayer.getPlayerColor

    filledFollower.onMouseExited = _ =>
      filledFollower.effect = null

    var x = 1
    var y = 1

    segment match
      case TileSegment.N => y -= 1
      case TileSegment.E => x += 1
      case TileSegment.S => y += 1
      case TileSegment.W => x -= 1
      case TileSegment.NE =>
        x += 1
        y -= 1
      case TileSegment.NW =>
        x -= 1
        y -= 1
      case TileSegment.SE =>
        x += 1
        y += 1
      case TileSegment.SW =>
        x -= 1
        y += 1
      case _ =>

    filledFollower.onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
      filledFollower.onMouseEntered = null
      filledFollower.onMouseExited = null
      filledFollower.effect = currentPlayer.getPlayerColor
      notifyFollowerPlacement(position, segment, currentPlayer)
      followerGrid.getChildren.removeIf(node =>
        GridPane.getColumnIndex(node) != x || GridPane.getRowIndex(node) != y
      )
      filledFollower.onMouseClicked = null

    followerGrid.add(stackPane, x, y)
  )

  this.maxWidth = 10
  this.maxHeight = 10
  this.children = Seq(drawnTileImage, followerGrid)
