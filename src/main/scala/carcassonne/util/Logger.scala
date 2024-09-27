package carcassonne.util

/**
 * Logging utility. 
 */
object Logger:

  /**
   * Logs a message to the console with a specified source.
   *
   * @param source  The source of the log message (e.g., class or method name).
   * @param message The message to be logged.
   */
  def log(source: String, message: String): Unit =
    println(s"$source - $message")
