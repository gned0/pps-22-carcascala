package carcassonne.view

import carcassonne.model.game.{GameState, Player}
import carcassonne.model.tile.TileSegment.N
import carcassonne.model.tile.{GameTile, TileSegment}
import carcassonne.observers.observers.{ObserverGameMatchBoard, ObserverGameMenuView}
import carcassonne.observers.subjects.view.{SubjectGameMatchView, SubjectGameMenuView, SubjectStarterView}
import carcassonne.util.{Logger, Position}
import javafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.{ColumnConstraints, GridPane, HBox, Priority, Region, RowConstraints, StackPane, VBox}
import scalafx.scene.text.Text
import scalafx.Includes.*
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle


/**
 * The view for the game map.
 * This class extends `GridPane` and implements `SubjectGameView` and `ObserverGameMap`.
 */
class GameMatchBoardView(gameEndedSwitchView: () => Unit) extends GridPane
  with ObserverGameMenuView
  with SubjectGameMatchView
  with ObserverGameMatchBoard:


  private val drawnTilePane = GridPane()
  drawnTilePane.alignment = Pos.CenterRight
  drawnTilePane.mouseTransparent = true

  this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
  this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE)
  this.setMaxSize(Double.MaxValue, Double.MaxValue)
  GridPane.setHgrow(this, Priority.Always)
  GridPane.setVgrow(this, Priority.Always)


  this.prefWidth = 600
  this.prefHeight = 400

  this.alignment = Pos.Center

  def initialize: Unit =
    notifyTilePlacementAttempt(GameTile.createStartTile(), Position(500, 500))
  /**
   * Places a tile at the specified position in the view.
 *
   * @param position the position where the tile should be placed
   * @param tiles the current state of the game map tiles
   */
  def placeTile(position: Position, tileGraphicElement: Node): Unit =
    // Remove the old placeholder
    this.getChildren.removeIf(node =>
      getColumnIndex(node) == position.x && getRowIndex(node) == position.y
    )
    this.add(tileGraphicElement, position.x, position.y)

  /**
   * Creates new placeholder tiles around the last placed tile.
   * @param tiles the current state of the game map tiles
   */
  def createNewPlaceholders(tiles: Map[Position, GameTile], position: Position): Unit =
    for
      posX <- Seq(position.x - 1, position.x + 1)
      if !tiles.contains(Position(posX, position.y))
    do
      val placeholderTile = createPlaceholderTile(Position(posX, position.y))

    for
      posY <- Seq(position.y - 1, position.y + 1)
      if !tiles.contains(Position(position.x, posY))
    do
      val placeholderTile = createPlaceholderTile(Position(position.x, posY))

  /**
   * Creates a placeholder tile at the specified position.
   * @param position the position where the placeholder tile should be created
   * @return the created placeholder tile
   */
  def createPlaceholderTile(position: Position): Region =
    new Region:
      prefWidth = 100
      prefHeight = 100
      styleClass += "placeholderTile"
      onMouseClicked = (event: MouseEvent) => if event.button == MouseButton.Primary then
        checkClickedTile(position)
      add(this, position.x, position.y)

  /**
   * Checks the clicked tile and notifies observers of a tile placement attempt.
   * @param position the position of the clicked tile
   */
  def checkClickedTile(position: Position): Unit =
    notifyTilePlacementAttempt(getDrawnTile._1, position)
  

  def getDrawnTilePane: Option[GridPane] = Some(drawnTilePane)


  /**
   * Called when a tile is placed on the game map.
   *
   * @param isTilePlaced whether the tile was successfully placed
   * @param tilesOption the current state of the game map tiles
   * @param position the position where the tile was placed
   */
  override def isTilePlaced(isTilePlaced: Boolean,
                            tilesOption: Option[Map[Position, GameTile]],
                            position: Position): Unit =
    val tiles = tilesOption.get
    if isTilePlaced then
        placeTile(position, getDrawnTile._2)
        createNewPlaceholders(tiles, position)

  override def gameEnded(players: List[Player]): Unit =
    GameEndView(players).popupStage.show()
    gameEndedSwitchView()

  override def isFollowerPlaced(position: Position, segment: TileSegment, player: Player): Unit = ()

  override def playerChanged(player: Player): Unit =
    setCurrentPlayer(player)

  override def availableFollowerPositions(availSegments: List[TileSegment], position: Position): Unit =
    val drawnTileImage = getDrawnTile._2

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

    availSegments.foreach( segment =>
      val followerImageView = new ImageView(new Image(getClass.getResource("../../follower.png").toExternalForm)):
        fitWidth = (drawnTileImage.fitWidth.toDouble - 5) / 3.3
        fitHeight = (drawnTileImage.fitHeight.toDouble - 5) / 3.3
        preserveRatio = true

      // Create the overlay meeple with the desired fill color
      val filledFollower = new ImageView(new Image(getClass.getResource("../../follower_filled.png").toExternalForm)):
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
        filledFollower.effect = getCurrentPlayer.getPlayerColor

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
        filledFollower.effect = getCurrentPlayer.getPlayerColor
        notifyFollowerPlacement(Position(x, y), segment, getCurrentPlayer)
        followerGrid.getChildren.removeIf(node =>
          GridPane.getColumnIndex(node) != x || GridPane.getRowIndex(node) != y
        )
        filledFollower.onMouseClicked = null

      followerGrid.add(stackPane, x, y)
    )

    val placeTileStackPane = new StackPane():
      maxHeight = 10
      maxWidth = 10
      children = Seq(
        drawnTileImage,
        followerGrid
      )

    placeTile(position, placeTileStackPane)

  override def scoreCalculated(position: Position, gameTile: GameTile): Unit =
    val graphicalTile: Option[javafx.scene.Node] = this.getChildrenUnmodifiable.toArray.find {
      case child: javafx.scene.Node => GridPane.getColumnIndex(child) == position.x && GridPane.getRowIndex(child) == position.y
    }.map(_.asInstanceOf[javafx.scene.Node])

    graphicalTile match
      case Some(s) if s.isInstanceOf[javafx.scene.layout.StackPane] =>
        val stackPane = s.asInstanceOf[javafx.scene.layout.StackPane]
        stackPane.getChildren.remove(1)
      case Some(_) => println("Other type of element")
      case None => println("No element")

  override def skipFollowerPlacement(position: Position): Unit =
    val graphicalTile: Option[javafx.scene.Node] = this.getChildrenUnmodifiable.toArray.find {
      case child: javafx.scene.Node => GridPane.getColumnIndex(child) == position.x && GridPane.getRowIndex(child) == position.y
    }.map(_.asInstanceOf[javafx.scene.Node])

    graphicalTile match
      case Some(s) if s.isInstanceOf[javafx.scene.layout.StackPane] =>
        val stackPane = s.asInstanceOf[javafx.scene.layout.StackPane]
        stackPane.getChildren.remove(1)
      case Some(_) => println("Other type of element")
      case None => println("No element")
    notifySkipFollowerPlacement()
