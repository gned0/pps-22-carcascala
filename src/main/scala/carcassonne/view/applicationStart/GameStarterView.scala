package carcassonne.view.applicationStart

import carcassonne.observers.subjects.view.SubjectStarterView
import javafx.scene.text.FontWeight
import scalafx.application.Platform
import scalafx.geometry.Pos.{Center, TopCenter}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{GridPane, HBox, StackPane, VBox}
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage

class GameStarterView(switchMainGameView: List[String] => Unit) extends StackPane
  with SubjectStarterView {

  private val gameLogo = new ImageView(new Image("carcaScala.png")):
    preserveRatio = true
    fitWidth = 700
    fitHeight = 600
    alignment = TopCenter
    padding = Insets(0, 0, 50, 0)
  private val startGameButton = new Button("Start Game"):
    alignment = Center
    minWidth = 100
    minHeight = 70
    font = Font("Arial", 18)
    padding = Insets(5)
  private val howToPlayButton = new Button("How To Play"):
    alignment = Center
    minWidth = 100
    minHeight = 70
    font = Font("Arial", 18)
    padding = Insets(5)
  private val exitGameButton = new Button("Exit Game"):
    alignment = Center
    minWidth = 100
    minHeight = 70
    font = Font("Arial", 18)
    padding = Insets(5)

  startGameButton.onMouseClicked = _ => showPlayerSetupDialog()
  exitGameButton.onMouseClicked = _ => Platform.exit()
  howToPlayButton.onMouseClicked = _ => showHowToPlayDialog()

  private val verticalButtonBox = new VBox(10) // 10 pixels of spacing between buttons
  verticalButtonBox.children.addAll(gameLogo, startGameButton, howToPlayButton, exitGameButton)
  verticalButtonBox.alignment = Pos.Center // Center the buttons in the HBox

//  this.children = Seq(startGameButton, exitGameButton)
  this.children = verticalButtonBox
  this.padding = Insets(10)
  StackPane.setAlignment(verticalButtonBox, Pos.Center)

  private def showPlayerSetupDialog(): Unit = {
    val playerSetupStage = new Stage {
      title = "Player Setup"
      scene = new Scene(240, 280) {
        root = createPlayerSetupPane()
      }
    }
    playerSetupStage.showAndWait()
  }

  private def createPlayerSetupPane(): VBox = {
    val playerNames = List.fill(5)(new TextField {
      promptText = "Enter player name"
      visible = false
    })

    val playerCountComboBox = new ComboBox[Int](2 to 5) {
      value = 2
      onAction = _ => {
        val selectedCount = value.value
        playerNames.zipWithIndex.foreach {
          case (textField, index) => textField.visible = index < selectedCount
        }
      }
    }

    playerNames.take(2).foreach(_.visible = true)

    val playerSetupPane = new VBox(10) {
      padding = Insets(20)
      alignment = Pos.Center

      children = Seq(
        new Label("Select number of players:"),
        playerCountComboBox
      ) ++ playerNames ++ Seq(
        new HBox(10) {
          alignment = Pos.Center
          children = Seq(
            new Button("Start Game") {
              onMouseClicked = _ => {
                val selectedNames = playerNames.filter(_.visible.value).map(_.text.value).filter(_.nonEmpty)
                if (selectedNames.size < 2) {
                  val alert = new Alert(AlertType.Warning) {
                    title = "Invalid Input"
                    headerText = "Not enough players"
                    contentText = "Please enter names for at least 2 players."
                  }
                  alert.showAndWait()
                } else {
                  switchMainGameView(selectedNames)
                  scene().getWindow.hide()
                }
              }
            },
            new Button("Cancel") {
              onMouseClicked = _ => scene().getWindow.hide()
            }
          )
        }
      )
    }
    playerSetupPane
  }

  private def showHowToPlayDialog(): Unit = {
    val howToPlayStage = new Stage {
      title = "How To Play"
      scene = new Scene(1024, 768) {
        root = createHowToPlayPane(this)
      }
    }
    howToPlayStage.showAndWait()
  }

  private def createHowToPlayPane(howToPlayStage: Scene): ScrollPane = {
    val contentBox = new VBox():
      margin = Insets(10)
      padding = Insets(10)
      alignment = TopCenter

    contentBox.children = Seq(
        new Text():
          alignment = Center
          font = Font("Arial", FontWeight.BOLD, 25)
          wrappingWidth <== contentBox.width - 20
          text =
            s"""GUI Description"""
        ,
        new Text():
          font = Font("Arial", 15)
          wrappingWidth <== contentBox.width - 20
          text =
            s"""The project consists of a digital version of the board game Carcassonne, where the current implementation supports matches played by 2 to 5 players. The image shown below shows an example of the GUI of an ongoing match, where each number represents different elements of the UI, which are explained also below:"""
        ,
        new ImageView(new Image("tutorial/tutorial1_icons.png")):
          margin = Insets(20, 0, 15, 0)
          preserveRatio = true
          fitWidth = 800
          fitHeight = 600
        ,
        new Text():
          font = Font("Arial", 15)
          wrappingWidth <== contentBox.width - 20
          text =
            s"""0. The UI part related to the currently active player, whom can place the newly drawn tile, rotate said tile clockwise or counter-clockwise, place a follower on the tile, skip the tile or follower placement or end the current game. It also shows the amount of available followers to place on the tiles;
1. This is tile drawn from the randomized deck of tiles, which the player can place on the board based on the borders of the tile drawn, and the borders of the tiles placed on the board, which have to match;
2. These buttons are used to, respectively from top left to bottom right, rotate the drawn counter-clockwise, clockwise, and/or skip the tile or follower placement, that is if the player wants to or is forced because either they don't have a place to to place the tile, or they don't have any followers;
3. This is the scoreboard, very simply show the score of each player;
4. This is the button used to end the game early, without showing the final score of the players;
5. This is the main board of the game, which can be zoomed in or out using the mouse wheel, and panned around using the mouse right button. Only the lighter background of the board is pannable, since the growth of the board itself is variable, the darker background is just used to blend better the environment;
6. This is the starter tile which is already placed on the board when starting a match.
7. This is a placeholder to show the possible placement of the tile that has been drawn, it's up to the player to decide to provide the correct orientation of the borders through the buttons on the right menu;
8. This is the what it looks like when trying to place a tile on the board, if the tile drawn matches the border/s of the tile/s already placed on the board, then the tile will be placed, otherwise it won't be.
9. This button is used to recenter the camera to the origin of the board, in case the player wants to reset the view after panning and/or zooming.

Below two more images are shown to give a visual representation of the follower placement: """
        ,
        new ImageView(new Image("tutorial/tutorial2.png")):
          margin = Insets(15, 0, 5, 0)
          preserveRatio = true
          fitWidth = 800
          fitHeight = 600
        ,
        new ImageView(new Image("tutorial/tutorial3.png")):
          margin = Insets(5, 0, 15, 0)
          preserveRatio = true
          fitWidth = 800
          fitHeight = 600
        ,
        new Text():
          font = Font("Arial", 15)
          wrappingWidth <== contentBox.width - 20
          text = s"""As we can see, the player can place the follower in any of the feature present on the tile, and later on at the end of every turn and at the end of the match, the score will be calculated and added for each completed feature as described in the basic Carcassone rules."""
        ,
        new Text():
          alignment = Center
          margin = Insets(25, 0, 5, 0)
          font = Font("Arial", FontWeight.BOLD, 25)
          wrappingWidth <== contentBox.width - 20
          text =
            s"""Follower Placement and Scoring Rules Description"""
        ,
        new Text():
          font = Font("Arial", 15)
          wrappingWidth <== contentBox.width - 20
          text =
            s"""The placement of followers will vary on the various features that actually permit the placement of one, based on the fact that the feature itself already is connected to another tile that has a follower placed on the same feature, as per base game rules. Some features can be hard to distinguish therefore the general exceptions are that:
1. Roads are usually placed in the cardinal directions (N, W, S or E) or the center of the tile, therefore it should be clear where the follower can be placed;
2. Cities are usually placed in the borders of tiles, and unless a tile has a big city that sprawls for multiple sections of the tile itself, it should be considere a field feature (e.g. There are tiles where the cities are very close to the borders, what is to be considered a city feature are the placements in the cardinal directions all the others are usually again to be considered fields);
3. Field should be pretty clear to see, again following the rule regarding the cities;
4. Monasteries should also have a pretty clear placement since they are usually placed in the center of the tile.
               """
    )




    new ScrollPane {
      content = contentBox
      vbarPolicy = ScrollPane.ScrollBarPolicy.Always // Always show vertical scrollbar
      hbarPolicy = ScrollPane.ScrollBarPolicy.Never  // Disable horizontal scrollbar
    }
  }

}
