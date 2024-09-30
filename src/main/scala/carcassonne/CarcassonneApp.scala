package carcassonne

import carcassonne.util.Logger
import carcassonne.view.GameViewContainer
import scalafx.application.JFXApp3

/**
 * The main application object for the Carcassonne game.
 * This object extends `JFXApp3` to create a JavaFX application.
 */
object CarcassonneApp extends JFXApp3:

  /**
   * The main entry point for the JavaFX application.
   * Initializes the model, view, and controller, and sets up the primary stage.
   */
  override def start(): Unit =
    Logger.activateLogger()
    GameStage(GameViewContainer())
