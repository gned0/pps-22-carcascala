package carcassonne.model

import scala.annotation.tailrec
import scala.collection.mutable

class ScoreCalculator {

  def calculateCityPoints(meepleSegment: TileSegment, 
                          position: Position, 
                          map: CarcassonneBoard): Int =
    val cityPoints = recursiveCityPointsCalculation(meepleSegment, position, map)
    if cityPoints != 0 then
      (cityPoints + 1) * 2
    else
      0

  def calculateFieldPoints(meepleSegment: TileSegment, 
                           position: Position, 
                           map: CarcassonneBoard): Int =
    recursiveFieldPointsCalculation(meepleSegment, position, map)

  def calculateRoadPoints(meepleSegment: TileSegment, 
                          position: Position, 
                          map: CarcassonneBoard): Int =
    recursiveRoadPointsCalculation(meepleSegment, position, map) + 1


  
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

  private def adjacentFieldSegments(segment: TileSegment, 
                                    gameTile: GameTile): Set[TileSegment] = {
    
    adjacencyMap(segment).filter { adjacentSegment =>
      gameTile.segments(adjacentSegment) == gameTile.segments(segment) && gameTile.segments(adjacentSegment) == SegmentType.Field
    }
  }


  private def adjacentFieldSegmentsAcrossTiles(segment: TileSegment, 
                                               tilePosition: Position, 
                                               map: CarcassonneBoard): Set[(Position, TileSegment)] = {
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

    
    adjacencies.filter { case (adjPosition, adjSegment) =>
      
      map.getTile(adjPosition).exists(_.segments(adjSegment) == SegmentType.Field)
    }
  }

  private def isAdjacentToCity(segment: TileSegment, 
                               tilePosition: Position, 
                               gameTile: GameTile, 
                               map: CarcassonneBoard): Set[(Position, TileSegment)] = {
    val intraTileAdjacentCities = adjacencyMap(segment).flatMap { adjSegment =>
      if (gameTile.segments.get(adjSegment).contains(SegmentType.City))
        Some((tilePosition, adjSegment))
      else
        None
    }
    
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
    
    val interTileAdjacentCities = interTileAdjacencies.flatMap { case (adjPosition, adjSegment) =>
      map.getTile(adjPosition).flatMap { adjTile =>
        if (adjTile.segments.get(adjSegment).contains(SegmentType.City))
          Some((adjPosition, adjSegment))
        else
          None
      }
    }
    intraTileAdjacentCities ++ interTileAdjacentCities
  }
  
  private def areSegmentsPartOfSameCity(citySegments: Set[(Position, TileSegment)], 
                                         map: CarcassonneBoard): Boolean = {
    if (citySegments.size <= 1) return true
    
    val visited = mutable.Set[(Position, TileSegment)]()
    val stack = mutable.Stack[(Position, TileSegment)]()
    val startSegment = citySegments.head
    stack.push(startSegment)
    visited.add(startSegment)

    while (stack.nonEmpty) {
      val (currentPos, currentSegment) = stack.pop()
      val adjacentCities = isAdjacentToCity(currentSegment, currentPos, map.getTile(currentPos).get, map)
      val validAdjacentCities = adjacentCities.intersect(citySegments)
      
      validAdjacentCities.foreach { adjCity =>
        if (!visited.contains(adjCity)) {
          visited.add(adjCity)
          stack.push(adjCity)
        }
      }
    }
    visited.size == citySegments.size
  }
  
  private def recursiveFieldPointsCalculation(meepleSegment: TileSegment, 
                                              position: Position, 
                                              map: CarcassonneBoard): Int =
    var citiesToCheck: List[(TileSegment, Position)] = List.empty
    var fieldsVisited: List[(TileSegment, Position)] = List.empty

    def exploreField(meepleSegment: TileSegment, position: Position): Unit =
      fieldsVisited = fieldsVisited :+ (meepleSegment, position)

      val currentTile = map.getTile(position).get
      
      for adjSegment <- adjacentFieldSegments(meepleSegment, currentTile) do
        if !fieldsVisited.contains((adjSegment, position)) then
          exploreField(adjSegment, position)
      
      for (adjPosition, adjSegment) <- adjacentFieldSegmentsAcrossTiles(meepleSegment, position, map) do
        if map.getTileMap.get.contains(adjPosition) && !fieldsVisited.contains(adjSegment, Position(adjPosition.x, adjPosition.y)) then
          exploreField(adjSegment, adjPosition)
      
      isAdjacentToCity(meepleSegment, position, currentTile, map).foreach((pos, seg) => if !citiesToCheck.contains((seg, pos)) then citiesToCheck = citiesToCheck :+ (seg, pos))
    
    exploreField(meepleSegment, position)

    val combinations = (2 to citiesToCheck.length).flatMap(citiesToCheck.combinations).
      filter(list => areSegmentsPartOfSameCity(list.map((seg, pos) => (pos, seg)).toSet, map)).
      map(_.init).filter(_.nonEmpty).flatten

    val results = citiesToCheck.filter((seg, pos) => !combinations.contains(seg, pos) &&
      calculateCityPoints(seg, pos, map) != 0)
    
    results.size * 3
  
  private def recursiveRoadPointsCalculation(meepleSegment: TileSegment, 
                                             position: Position, 
                                             map: CarcassonneBoard): Int =
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
  
  private def recursiveCityPointsCalculation(meepleSegment: TileSegment, 
                                             position: Position, 
                                             map: CarcassonneBoard): Int =
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
}
