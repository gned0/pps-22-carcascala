package carcassonne.model.board

import carcassonne.model.*
import carcassonne.model.game.Player
import carcassonne.model.tile.SegmentType.{City, Road, RoadEnd}
import carcassonne.model.tile.TileSegment
import carcassonne.model.tile.TileSegment._
import carcassonne.model.tile.{GameTile, SegmentType, TileSegment}
import carcassonne.util.{Logger, Position}

import scala.collection.mutable
import scala.util.{Try, Success, Failure}

/**
 * Trait representing the Carcassonne game board.
 */
trait CarcassonneBoard:
  /**
   * Places a tile on the board at the specified position.
   *
   * @param tile the tile to place
   * @param position the position on the board to place the tile
   * @return true if the tile was placed successfully, false otherwise
   */
  def placeTile(tile: GameTile, position: Position): Boolean

  /**
   * Retrieves the tile at the specified position.
   *
   * @param position the position on the board
   * @return an Option containing the tile if present, None otherwise
   */
  def getTile(position: Position): Option[GameTile]

  /**
   * Retrieves the map of all tiles on the board.
   *
   * @return an Option containing the map of positions to tiles if the board is not empty, None otherwise
   */
  def getTileMap: Option[Map[Position, GameTile]]

  /**
   * Places a follower on a specific segment of a tile at the given position.
   *
   * @param position the position on the board where the follower is placed
   * @param segment the segment of the tile where the follower is placed
   * @param player the player placing the follower
   * @return true if the follower was placed successfully, false otherwise
   */
  def placeFollower(position: Position, segment: TileSegment, player: Player): Boolean

  /**
   * Removes a follower from the specified tile.
   *
   * @param gameTile the tile from which to remove the follower
   * @return true if the follower was removed successfully, false otherwise
   */
  def removeFollower(gameTile: GameTile): Boolean

  /**
   * Retrieves the connected feature starting from the specified position and segment.
   *
   * @param startPosition the starting position
   * @param startSegment the starting segment
   * @return a set of positions and segments representing the connected feature
   */
  def getConnectedFeature(startPosition: Position, startSegment: TileSegment): Set[(Position, TileSegment)]

/**
 * Companion object for the CarcassonneBoard trait.
 */
object CarcassonneBoard:
  /**
   * Creates an instance of CarcassonneBoard.
   *
   * @return an instance of CarcassonneBoard
   */
  def apply(): CarcassonneBoard = new CarcassonneBoardImpl()

  /**
   * Private implementation of the CarcassonneBoard trait.
   */
  private class CarcassonneBoardImpl extends CarcassonneBoard:
    private val board: mutable.Map[Position, GameTile] = mutable.Map.empty

    /**
     * Places a tile on the board at the specified position.
     *
     * @param tile the tile to place
     * @param position the position on the board to place the tile
     * @return true if the tile was placed successfully, false otherwise
     * @throws IllegalArgumentException if a tile is already placed at the position or the placement is invalid
     */
    def placeTile(tile: GameTile, position: Position): Boolean =
      if board.contains(position) then throw IllegalArgumentException(s"Tile already placed at position $position")
      else if isValidTilePlacement(tile, position) then
        board(position) = tile
        Logger.log("MODEL", s"Tile placed at $position")
        true
      else throw IllegalArgumentException(s"Invalid tile placement at position $position")

    /**
     * Checks if the tile placement is valid by comparing it with neighboring tiles.
     *
     * @param tile the tile to place
     * @param position the position on the board to place the tile
     * @return true if the tile placement is valid, false otherwise
     */
    private def isValidTilePlacement(tile: GameTile, position: Position): Boolean =
      val neighborPositions = List(
        (Position(position.x, position.y - 1), N, S),
        (Position(position.x + 1, position.y), E, W),
        (Position(position.x, position.y + 1), S, N),
        (Position(position.x - 1, position.y), W, E)
      )

      neighborPositions.forall { case (pos, tileSegment, neighborSegment) =>
        board.get(pos).forall { neighborTile =>
          tile.segments(tileSegment) == neighborTile.segments(neighborSegment)
        }
      }

    /**
     * Retrieves the tile at the specified position.
     *
     * @param position the position on the board
     * @return an Option containing the tile if present, None otherwise
     */
    def getTile(position: Position): Option[GameTile] = board.get(position)

    /**
     * Retrieves the map of all tiles on the board.
     *
     * @return an Option containing the map of positions to tiles if the board is not empty, None otherwise
     */
    def getTileMap: Option[Map[Position, GameTile]] =
      Option.when(board.nonEmpty)(board.toMap)

    /**
     * Places a follower on a specific segment of a tile at the given position.
     *
     * @param position the position on the board where the follower is placed
     * @param segment the segment of the tile where the follower is placed
     * @param player the player placing the follower
     * @return true if the follower was placed successfully, false otherwise
     */
    def placeFollower(position: Position, segment: TileSegment, player: Player): Boolean =
      if player.getFollowers == 0 then return false

      getTile(position) match
        case Some(tile) if tile.getFollowerMap.contains(segment) => false
        case Some(tile) =>
          val connectedFeature = getConnectedFeature(position, segment)
          val isFeatureOccupied = connectedFeature.exists { case (pos, seg) =>
            getTile(pos).exists(_.getFollowerMap.contains(seg))
          }
          if !isFeatureOccupied then
            tile.placeFollower(segment, player.playerId)
            true
          else false
        case None => false

    /**
     * Removes a follower from the specified tile.
     *
     * @param gameTile the tile from which to remove the follower
     * @return true if the follower was removed successfully, false otherwise
     */
    def removeFollower(gameTile: GameTile): Boolean =
      gameTile.getFollowerMap.keys.foreach(gameTile.removeFollower)
      true

    /**
     * Retrieves the connected feature starting from the specified position and segment.
     *
     * @param startPosition the starting position
     * @param startSegment the starting segment
     * @return a set of positions and segments representing the connected feature
     */
    def getConnectedFeature(startPosition: Position, startSegment: TileSegment): Set[(Position, TileSegment)] =
      var visited: Set[(Position, TileSegment)] = Set.empty
      val result: mutable.Set[(Position, TileSegment)] = mutable.Set.empty

      def dfs(position: Position, segment: TileSegment): Unit =
        if !board.contains(position) || board(position).segments(segment) == RoadEnd then return

        visited += ((position, segment))

        board.get(position).foreach { currentTile =>
          result += ((position, segment))

          val segmentType = currentTile.segments(segment)

          segmentType match
            case Road =>
              getAdjacentRoadTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if !visited.contains((adjPosition, adjSegment)) then
                  dfs(adjPosition, adjSegment)
              )
            case City =>
              getAdjacentCityTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if !visited.contains((adjPosition, adjSegment)) then
                  dfs(adjPosition, adjSegment)
              )
            case _ =>
              getAdjacentTileSegments(segment, position, segmentType).foreach((adjPosition, adjSegment) =>
                if !visited.contains((adjPosition, adjSegment)) then
                  dfs(adjPosition, adjSegment)
              )
        }

      dfs(startPosition, startSegment)
      result.toSet

    /**
     * Retrieves adjacent city tile segments for the given segment and position.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent city tile segments
     */
    private def getAdjacentCityTileSegments(
                                             segment: TileSegment,
                                             tilePosition: Position,
                                             segmentType: SegmentType
                                           ): Set[(Position, TileSegment)] =
      adjacentCitySegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentCitySegmentsAcrossTiles(segment, tilePosition, segmentType)

    /**
     * Retrieves adjacent city segments within the current tile.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent city segments within the current tile
     */
    private def adjacentCitySegmentsCurrentTile(
                                                 segment: TileSegment,
                                                 tilePosition: Position,
                                                 segmentType: SegmentType
                                               ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case NW =>
          Set(
            (Position(tilePosition.x - 1, tilePosition.y), NE),
            (Position(tilePosition.x, tilePosition.y - 1), SW)
          )
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case NE =>
          Set(
            (Position(tilePosition.x + 1, tilePosition.y), NW),
            (Position(tilePosition.x, tilePosition.y - 1), SE)
          )
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case C => Set.empty
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case TileSegment.SW =>
          Set(
            (Position(tilePosition.x - 1, tilePosition.y), SE),
            (Position(tilePosition.x, tilePosition.y + 1), NW)
          )
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), TileSegment.N))
        case SE =>
          Set(
            (Position(tilePosition.x + 1, tilePosition.y), SW),
            (Position(tilePosition.x, tilePosition.y + 1), NE)
          )
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Retrieves adjacent city segments across tiles.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent city segments across tiles
     */
    private def adjacentCitySegmentsAcrossTiles(
                                                 segment: TileSegment,
                                                 tilePosition: Position,
                                                 segmentType: SegmentType
                                               ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case NW =>
          Set(
            (tilePosition, N),
            (tilePosition, W)
          )
        case N =>
          Set(
            (tilePosition, NW),
            (tilePosition, C),
            (tilePosition, NE)
          )
        case NE =>
          Set(
            (tilePosition, N),
            (tilePosition, E)
          )
        case W =>
          Set(
            (tilePosition, NW),
            (tilePosition, C),
            (tilePosition, SW)
          )
        case C =>
          Set(
            (tilePosition, N),
            (tilePosition, W),
            (tilePosition, E),
            (tilePosition, S)
          )
        case E =>
          Set(
            (tilePosition, NE),
            (tilePosition, C),
            (tilePosition, SE)
          )
        case SW =>
          Set(
            (tilePosition, W),
            (tilePosition, S)
          )
        case S =>
          Set(
            (tilePosition, SW),
            (tilePosition, C),
            (tilePosition, SE)
          )
        case SE =>
          Set(
            (tilePosition, E),
            (tilePosition, S)
          )
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Retrieves adjacent road tile segments for the given segment and position.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent road tile segments
     */
    private def getAdjacentRoadTileSegments(
                                             segment: TileSegment,
                                             tilePosition: Position,
                                             segmentType: SegmentType
                                           ): Set[(Position, TileSegment)] =
      adjacentRoadSegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentRoadSegmentsAcrossTiles(segment, tilePosition, segmentType)

    /**
     * Retrieves adjacent road segments within the current tile.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent road segments within the current tile
     */
    private def adjacentRoadSegmentsCurrentTile(
                                                 segment: TileSegment,
                                                 tilePosition: Position,
                                                 segmentType: SegmentType
                                               ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case N => Set((tilePosition, C))
        case W => Set((tilePosition, C))
        case C =>
          Set(
            (tilePosition, N),
            (tilePosition, E),
            (tilePosition, S),
            (tilePosition, W)
          )
        case E => Set((tilePosition, C))
        case S => Set((tilePosition, C))
        case _ => Set.empty
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Retrieves adjacent road segments across tiles.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent road segments across tiles
     */
    private def adjacentRoadSegmentsAcrossTiles(
                                                 segment: TileSegment,
                                                 tilePosition: Position,
                                                 segmentType: SegmentType
                                               ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), N))
        case _ => Set.empty
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Retrieves adjacent tile segments for the given segment and position.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent tile segments
     */
    private def getAdjacentTileSegments(
                                         segment: TileSegment,
                                         tilePosition: Position,
                                         segmentType: SegmentType
                                       ): Set[(Position, TileSegment)] =
      adjacentSegmentsCurrentTile(segment, tilePosition, segmentType) union
        adjacentSegmentsAcrossTiles(segment, tilePosition, segmentType)

    /**
     * Retrieves adjacent segments within the current tile.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent segments within the current tile
     */
    private def adjacentSegmentsCurrentTile(
                                             segment: TileSegment,
                                             tilePosition: Position,
                                             segmentType: SegmentType
                                           ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case NW =>
          Set(
            (Position(tilePosition.x - 1, tilePosition.y - 1), SE),
            (Position(tilePosition.x - 1, tilePosition.y), NE),
            (Position(tilePosition.x, tilePosition.y - 1), SW)
          )
        case N => Set((Position(tilePosition.x, tilePosition.y - 1), S))
        case NE =>
          Set(
            (Position(tilePosition.x + 1, tilePosition.y - 1), SW),
            (Position(tilePosition.x + 1, tilePosition.y), NW),
            (Position(tilePosition.x, tilePosition.y - 1), SE)
          )
        case W => Set((Position(tilePosition.x - 1, tilePosition.y), E))
        case C => Set.empty
        case E => Set((Position(tilePosition.x + 1, tilePosition.y), W))
        case SW =>
          Set(
            (Position(tilePosition.x - 1, tilePosition.y + 1), NE),
            (Position(tilePosition.x - 1, tilePosition.y), SE),
            (Position(tilePosition.x, tilePosition.y + 1), NW)
          )
        case S => Set((Position(tilePosition.x, tilePosition.y + 1), N))
        case SE =>
          Set(
            (Position(tilePosition.x + 1, tilePosition.y + 1), NW),
            (Position(tilePosition.x + 1, tilePosition.y), SW),
            (Position(tilePosition.x, tilePosition.y + 1), NE)
          )
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Retrieves adjacent segments across tiles.
     *
     * @param segment the segment of the tile
     * @param tilePosition the position of the tile
     * @param segmentType the type of the segment
     * @return a set of positions and segments representing adjacent segments across tiles
     */
    private def adjacentSegmentsAcrossTiles(
                                             segment: TileSegment,
                                             tilePosition: Position,
                                             segmentType: SegmentType
                                           ): Set[(Position, TileSegment)] =
      val adjacencies: Set[(Position, TileSegment)] = segment match
        case NW =>
          Set(
            (tilePosition, N),
            (tilePosition, C),
            (tilePosition, W)
          )
        case N =>
          Set(
            (tilePosition, NW),
            (tilePosition, W),
            (tilePosition, C),
            (tilePosition, E),
            (tilePosition, NE)
          )
        case NE =>
          Set(
            (tilePosition, N),
            (tilePosition, C),
            (tilePosition, E)
          )
        case W =>
          Set(
            (tilePosition, NW),
            (tilePosition, N),
            (tilePosition, C),
            (tilePosition, S),
            (tilePosition, SW)
          )
        case C =>
          Set(
            (tilePosition, NE),
            (tilePosition, N),
            (tilePosition, NE),
            (tilePosition, W),
            (tilePosition, E),
            (tilePosition, SE),
            (tilePosition, S),
            (tilePosition, SE)
          )
        case E =>
          Set(
            (tilePosition, NE),
            (tilePosition, N),
            (tilePosition, C),
            (tilePosition, S),
            (tilePosition, SE)
          )
        case TileSegment.SW =>
          Set(
            (tilePosition, W),
            (tilePosition, C),
            (tilePosition, S)
          )
        case S =>
          Set(
            (tilePosition, SW),
            (tilePosition, W),
            (tilePosition, C),
            (tilePosition, E),
            (tilePosition, SE)
          )
        case SE =>
          Set(
            (tilePosition, E),
            (tilePosition, C),
            (tilePosition, S)
          )
      filterSegmentTypes(adjacencies, segmentType)

    /**
     * Filters the given set of adjacent positions and segments to include only those that match the specified segment type.
     *
     * This method takes a set of adjacent positions and segments and filters it to include only those positions and segments
     * where the segment type of the tile at the adjacent position matches the specified segment type.
     *
     * @param adjacencySet the set of adjacent positions and segments to filter
     * @param segmentType  the segment type to filter by
     * @return a set of positions and segments where the segment type matches the specified segment type
     * */
    private def filterSegmentTypes(
                                    adjacencySet: Set[(Position, TileSegment)],
                                    segmentType: SegmentType
                                  ): Set[(Position, TileSegment)] =
      adjacencySet.filter { case (adjPosition, adjSegment) =>
        getTile(adjPosition).exists(_.segments(adjSegment) == segmentType)
      }