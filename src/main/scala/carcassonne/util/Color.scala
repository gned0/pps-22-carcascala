package carcassonne.util

enum Color:
  case Black, Red, Yellow, Green, Blue, Purple

object PlayerColor: 

  // Define a list of available colors
  private val availableColors = List(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Purple)

  /**
   * Assigns unique colors to a list of player names.
   *
   * @param playerNames A list of player names.
   * @return A list of tuples, where each tuple contains a player name and a color.
   */
  def assignColors(playerNames: List[String]): List[(String, Color)] = {
    playerNames.zipWithIndex.map {
      case (name, index) =>
        val color = availableColors(index % availableColors.size)
        (name, color)
    }
  }


