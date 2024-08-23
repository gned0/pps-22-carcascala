package carcassonne.model

import carcassonne.observers.SubjectGameMatch

object GameMatch:
  private val MinPlayers = 2

class GameMatch(players: List[Player], map: GameMap, deck: TileDeck) extends SubjectGameMatch[GameMatch]:
  require(players.length >= GameMatch.MinPlayers, s"At least ${GameMatch.MinPlayers} players are required to start the game.")

  private var currentPlayerIndex: Int = 0

  private def currentPlayer: Player = players(currentPlayerIndex)

  private def endTurn(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length

  private def isGameOver: Boolean = deck.isEmpty

  private def takeTurn(): Unit =
    val tile = deck.draw()
    notifyTileDrawn(tile.get)
    // map.placeTile(tile.get, Position(userInput))
    // map.placeFollower(Position(userInput)
    // scoring.computeScore(map)

//    endTurn()

  def play(): Unit =
      takeTurn()

  def gameEnded(): Unit =
    println("Game over! Final scores:")
    players.foreach(p => println(s"${p.name}: ${p.score}"))


