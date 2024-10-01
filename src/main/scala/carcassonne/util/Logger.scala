package carcassonne.util

import scala.util.Try

/** Logging utility using Scala 3 idioms and advanced constructs.
  */
object Logger:

  private var activated: Boolean = false

  /** Activates the logger.
    *
    * @return
    *   A `Try[Unit]` indicating the success or failure of the operation.
    */
  def activateLogger(): Try[Unit] = Try {
    activated = true
  }

  /** Deactivates the logger.
    *
    * @return
    *   A `Try[Unit]` indicating the success or failure of the operation.
    */
  def deactivateLogger(): Try[Unit] = Try {
    activated = false
  }

  /** Logs a message to the console with a specified source.
    *
    * @param source
    *   The source of the log message (e.g., class or method name).
    * @param message
    *   The message to be logged.
    * @return
    *   A `Try[Unit]` indicating the success or failure of the logging operation.
    */
  def log(source: String, message: String): Try[Unit] = Try {
    if activated then println(s"$source - $message")
  }
