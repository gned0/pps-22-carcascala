package carcassonne.model

import carcassonne.observers.SubjectGameMatch
import carcassonne.model.TileSegment.{C, E, N, S, W}

import scala.annotation.tailrec
import scala.collection.mutable

object GameMatch:
  private val MinPlayers = 2

class GameMatch(players: List[Player], map: CarcassonneBoard, deck: TileDeck) extends SubjectGameMatch[GameMatch]:
  require(players.length >= GameMatch.MinPlayers, s"At least ${GameMatch.MinPlayers} players are required to start the game.")

  private var currentPlayerIndex: Int = 0

  private def currentPlayer: Player = players(currentPlayerIndex)

  private def endTurn(): Unit =
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length

  private def isGameOver: Boolean = deck.isEmpty

  private def takeTurn(): Unit =
    if deck.isEmpty then
      gameEnded()
    else
      val tile = deck.draw()
      notifyTileDrawn(tile.get)
    // map.placeTile(tile.get, Position(userInput))
    // map.placeFollower(Position(userInput)
    // scoring.computeScore(map)

//    endTurn()

  def placeTile(gameTile: GameTile, position: Position): Unit =
    val isTilePlaced = map.placeTile(gameTile, position)
    notifyIsTilePlaced(isTilePlaced, map.getTileMap, position)

    endTurn()
    takeTurn()

  def play(): Unit =
    takeTurn()

  def gameEnded(): Unit =
    notifyGameEnded(players)
    println("Game over! Final scores:")
    players.foreach(p => println(s"${p.name}: ${p.score}"))



  def placeMeeple(gameTile: GameTile, segment: TileSegment, player: Player): Boolean =
    if !gameTile.followerMap.contains(segment) then
      gameTile.followerMap = gameTile.followerMap.updated(segment, Some(player).get.playerId)
      true
    else false


  // Define the direct adjacencies for each TileSegment
  private val adjacencyMap: Map[TileSegment, Set[TileSegment]] = Map(
    TileSegment.NW -> Set(TileSegment.N, TileSegment.W, TileSegment.C),
    TileSegment.N -> Set(TileSegment.NW, TileSegment.NE, TileSegment.C),
    TileSegment.NE -> Set(TileSegment.N, TileSegment.E, TileSegment.C),
    TileSegment.W -> Set(TileSegment.NW, TileSegment.SW, TileSegment.C),
    TileSegment.C -> Set(TileSegment.NW, TileSegment.N, TileSegment.NE, TileSegment.W, TileSegment.E, TileSegment.SW, TileSegment.S, TileSegment.SE),
    TileSegment.E -> Set(TileSegment.NE, TileSegment.SE, TileSegment.C),
    TileSegment.SW -> Set(TileSegment.W, TileSegment.S, TileSegment.C),
    TileSegment.S -> Set(TileSegment.SW, TileSegment.SE, TileSegment.C),
    TileSegment.SE -> Set(TileSegment.S, TileSegment.E, TileSegment.C)
  )

  def adjacentFieldSegments(segment: TileSegment, gameTile: GameTile): Set[TileSegment] = {
    // Filter only the segments that are connected fields
    adjacencyMap(segment).filter { adjacentSegment =>
      gameTile.segments(adjacentSegment) == gameTile.segments(segment) && gameTile.segments(adjacentSegment) == SegmentType.Field
    }
  }


  def adjacentFieldSegmentsAcrossTiles(segment: TileSegment, tilePosition: Position): Set[(Position, TileSegment)] = {
    val adjacencies: Set[(Position, TileSegment)] = segment match {
      case TileSegment.NW => Set(
        (Position(tilePosition.x - 1, tilePosition.y - 1), TileSegment.SE),
        (Position(tilePosition.x - 1, tilePosition.y), TileSegment.NE),
        (Position(tilePosition.x, tilePosition.y - 1), TileSegment.SW)
      )
      case TileSegment.N => Set((Position(tilePosition.x, tilePosition.y - 1), TileSegment.S))
      case TileSegment.NE => Set(
        (Position(tilePosition.x + 1, tilePosition.y - 1), TileSegment.SW),
        (Position(tilePosition.x + 1, tilePosition.y), TileSegment.NW),
        (Position(tilePosition.x, tilePosition.y - 1), TileSegment.SE),
      )
      case TileSegment.W => Set((Position(tilePosition.x - 1, tilePosition.y), TileSegment.E))
      case TileSegment.C => Set.empty
      case TileSegment.E => Set((Position(tilePosition.x + 1, tilePosition.y), TileSegment.W))
      case TileSegment.SW => Set(
        (Position(tilePosition.x - 1, tilePosition.y + 1), TileSegment.NE),
        (Position(tilePosition.x - 1, tilePosition.y), TileSegment.SE),
        (Position(tilePosition.x, tilePosition.y + 1), TileSegment.NW)
      )
      case TileSegment.S => Set((Position(tilePosition.x, tilePosition.y + 1), TileSegment.N))
      case TileSegment.SE => Set(
        (Position(tilePosition.x + 1, tilePosition.y + 1), TileSegment.NW),
        (Position(tilePosition.x + 1, tilePosition.y), TileSegment.SW),
        (Position(tilePosition.x, tilePosition.y + 1), TileSegment.NE)
      )
    }

    // Return only the positions that connect to a field segment in the adjacent tile
    adjacencies.filter { case (adjPosition, adjSegment) =>
      // If the adjacent tile exists and has a field segment, include it
      map.getTile(adjPosition).exists(_.segments(adjSegment) == SegmentType.Field)
    }
  }

  def isAdjacentToCity(segment: TileSegment, tilePosition: Position, gameTile: GameTile): Set[(Position, TileSegment)] = {
    // Check for intra-tile adjacency
    val intraTileAdjacentCities = adjacencyMap(segment).flatMap { adjSegment =>
      if (gameTile.segments.get(adjSegment).contains(SegmentType.City))
        Some((tilePosition, adjSegment))
      else
        None
    }

    // Determine inter-tile adjacencies
    val interTileAdjacencies = segment match {
      case TileSegment.NW => Set((Position(tilePosition._1 - 1, tilePosition._2 - 1), TileSegment.SE))
      case TileSegment.N => Set((Position(tilePosition._1, tilePosition._2 - 1), TileSegment.S))
      case TileSegment.NE => Set((Position(tilePosition._1 + 1, tilePosition._2 - 1), TileSegment.SW))
      case TileSegment.W => Set((Position(tilePosition._1 - 1, tilePosition._2), TileSegment.E))
      case TileSegment.C => Set.empty
      case TileSegment.E => Set((Position(tilePosition._1 + 1, tilePosition._2), TileSegment.W))
      case TileSegment.SW => Set((Position(tilePosition._1 - 1, tilePosition._2 + 1), TileSegment.NE))
      case TileSegment.S => Set((Position(tilePosition._1, tilePosition._2 + 1), TileSegment.N))
      case TileSegment.SE => Set((Position(tilePosition._1 + 1, tilePosition._2 + 1), TileSegment.NW))
    }

    // Check if any adjacent tile segment contains a city
    val interTileAdjacentCities = interTileAdjacencies.flatMap { case (adjPosition, adjSegment) =>
      map.getTile(adjPosition).flatMap { adjTile =>
        if (adjTile.segments.get(adjSegment).contains(SegmentType.City))
          Some((adjPosition, adjSegment))
        else
          None
      }
    }

    // Return the set of all adjacent city segments (both intra and inter-tile)
    intraTileAdjacentCities ++ interTileAdjacentCities
  }


  // Checks if the given city segments are part of the same city.
  def areSegmentsPartOfSameCity(
                                 citySegments: Set[(Position, TileSegment)]
                               ): Boolean = {

    // Early return if there's only one segment
    if (citySegments.size <= 1) return true

    // To keep track of visited city segments
    val visited = mutable.Set[(Position, TileSegment)]()

    // Stack for DFS (could also use a queue for BFS)
    val stack = mutable.Stack[(Position, TileSegment)]()

    // Start with one of the segments
    val startSegment = citySegments.head
    stack.push(startSegment)
    visited.add(startSegment)

    while (stack.nonEmpty) {
      val (currentPos, currentSegment) = stack.pop()

      // Get all adjacent city segments for the current segment
      val adjacentCities = isAdjacentToCity(currentSegment, currentPos, map.getTile(currentPos).get)

      // Filter only those adjacent segments that are part of the input citySegments
      val validAdjacentCities = adjacentCities.filter(citySegments.contains)

      // Traverse each connected city segment
      validAdjacentCities.foreach { adjCity =>
        if (!visited.contains(adjCity)) {
          visited.add(adjCity)
          stack.push(adjCity)
        }
      }
    }

    // If all city segments were visited, they are part of the same city
    visited.size == citySegments.size
  }

  def calculateFieldPoints(meepleSegment: TileSegment, position: Position): Int =
    recursiveFieldPointsCalculation(meepleSegment, position)

  private def recursiveFieldPointsCalculation(meepleSegment: TileSegment, position: Position): Int =
    var citiesToCheck: List[(TileSegment, Position)] = List.empty
    var fieldsVisited: List[(TileSegment, Position)] = List.empty

    def exploreField(meepleSegment: TileSegment, position: Position): Unit =
      fieldsVisited = fieldsVisited :+ (meepleSegment, position)

      val currentTile = map.getTile(position).get
      // Check adjacent segments within the same tile
      for adjSegment <- adjacentFieldSegments(meepleSegment, currentTile) do
        if !fieldsVisited.contains((adjSegment, position)) then
          exploreField(adjSegment, position)

      // Check adjacent tiles and their segments
      for (adjPosition, adjSegment) <- adjacentFieldSegmentsAcrossTiles(meepleSegment, position) do
        if map.getTileMap.get.contains(adjPosition) && !fieldsVisited.contains(adjSegment, Position(adjPosition.x, adjPosition.y)) then
          exploreField(adjSegment, adjPosition)

      // If this segment is adjacent to a city, add the city to the cities set
      isAdjacentToCity(meepleSegment, position, currentTile).foreach((pos, seg) => if !citiesToCheck.contains((seg, pos)) then citiesToCheck = citiesToCheck :+ (seg, pos))

    // Start exploration from the initial segment
    exploreField(meepleSegment, position)


    val combinations = (2 to citiesToCheck.length).flatMap(citiesToCheck.combinations).
      filter(list => areSegmentsPartOfSameCity(list.map((seg, pos) => (pos, seg)).toSet)).
      map(_.init).filter(_.nonEmpty).flatten

    println(combinations)
    println(citiesToCheck)

    val results = citiesToCheck.filter((seg, pos) => !combinations.contains(seg, pos) && 
      calculateCityPoints(seg, pos) != 0)


//      .map((seg, pos) => calculateCityPoints(seg, pos))


    // Return the score based on the number of cities connected to the field
    results.size * 3

  def calculateRoadPoints(meepleSegment: TileSegment, position: Position): Int =
    recursiveRoadPointsCalculation(meepleSegment, position) + 1

  private def recursiveRoadPointsCalculation(meepleSegment: TileSegment, position: Position): Int =
    val originalPos = position
    var exploredPositions: List[Position] = List.empty
    
    @tailrec
    def helper(segmentsToCheck: List[(TileSegment, Position)], acc: Int): Int =
      segmentsToCheck match
        case Nil => acc
        case (segment, pos) :: tail =>

          val tilesToCheck: List[(TileSegment, Position)] =
              List(
              (TileSegment.N, Position(pos.x, pos.y - 1)),
              (TileSegment.S, Position(pos.x, pos.y + 1)),
              (TileSegment.W, Position(pos.x - 1, pos.y)),
              (TileSegment.E, Position(pos.x + 1, pos.y))
            ).filter { case (seg, checkPos) =>
              map.getTileMap.get.get(pos).exists(_.segments(seg) == SegmentType.Road 
                && !exploredPositions.contains(checkPos))
            }.map((seg, pos) => seg match
              case TileSegment.N => (TileSegment.S, pos)
              case TileSegment.S => (TileSegment.N, pos)
              case TileSegment.W => (TileSegment.E, pos)
              case TileSegment.E => (TileSegment.W, pos)
            )

          tilesToCheck.foldLeft((tail, acc)) { case ((remainingSegments, currentAcc), (seg, checkTilePosition)) =>
            exploredPositions = exploredPositions :+ checkTilePosition
            map.getTileMap.get.get(checkTilePosition) match
              case Some(tile) if tile.segments(seg) == SegmentType.Road
                && tile.segments(TileSegment.C) == SegmentType.RoadEnd =>
                (remainingSegments, currentAcc + 1)
              case Some(tile) if checkTilePosition == originalPos =>
                (remainingSegments, currentAcc)
              case Some(tile) =>
                val newSegments = tile.segments.collect {
                  case (newSeg, SegmentType.Road) if List(TileSegment.N,
                    TileSegment.S,
                    TileSegment.W,
                    TileSegment.E).contains(newSeg) && newSeg != seg =>
                    (newSeg, checkTilePosition)
                }.toList
                (newSegments ++ remainingSegments, currentAcc + 1)
              case None =>
                (remainingSegments, currentAcc)
          } match {
            case (newSegments, newAcc) => helper(newSegments, newAcc)
          }

    helper(List((meepleSegment, position)), 0)




  def calculateCityPoints(meepleSegment: TileSegment, position: Position): Int =
    val cityPoints = recursiveCityPointsCalculation(meepleSegment, position)
    if cityPoints != 0 then
      (cityPoints + 1) * 2
    else
      0

  private def recursiveCityPointsCalculation(meepleSegment: TileSegment, position: Position): Int =

    @tailrec
    def helper(segmentsToCheck: List[(TileSegment, Position)], acc: Int): Int =
      segmentsToCheck match
        case Nil => acc
        case (segment, pos) :: tail =>
          val checkTilePosition = segment match
            case TileSegment.N => Position(pos.x, pos.y - 1)
            case TileSegment.S => Position(pos.x, pos.y + 1)
            case TileSegment.W => Position(pos.x - 1, pos.y)
            case TileSegment.E => Position(pos.x + 1, pos.y)
            case _ => pos
  
          val cityOrientation = segment match
            case TileSegment.N => TileSegment.S
            case TileSegment.S => TileSegment.N
            case TileSegment.W => TileSegment.E
            case TileSegment.E => TileSegment.W
            case _ => segment

          map.getTileMap.get.get(checkTilePosition) match
            case Some(tile) if tile.segments(cityOrientation) == SegmentType.City && tile.segments(TileSegment.C) != SegmentType.City =>
              helper(tail, acc + 1)
            case Some(tile) =>
              val newSegments = tile.segments.collect {
                case (seg, SegmentType.City) if List(TileSegment.N, TileSegment.S, TileSegment.W, TileSegment.E).contains(seg) && seg != cityOrientation =>
                  (seg, checkTilePosition)
              }.toList
              helper(newSegments ++ tail, acc + 1)
            case None =>
              helper(tail, acc)
            
    helper(List((meepleSegment, position)), 0)