package carcassonne.util

object Logger :
  def log(source: String, message: String): Unit =
    println(s"$source - $message")
