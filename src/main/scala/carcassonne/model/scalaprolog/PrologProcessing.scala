package carcassonne.model.scalaprolog

import alice.tuprolog.Term
import carcassonne.model.board.CarcassonneBoard
import carcassonne.util.ScalaPrologUtils.*
import carcassonne.model.scalaprolog.ScalaPrologEngine.{*, given}
import carcassonne.model.tile.TileSegment
import carcassonne.util.Position

/** A class responsible for processing Prolog queries to determine the completion of specific features (cities, roads, monasteries). It interacts with the Prolog engine
  * using game data and Prolog clauses.
  */
object PrologProcessing:

  /** Checks if a city feature is completed.
    *
    * @param map
    *   The `CarcassonneBoard` containing the game state.
    * @param connectedFeatures
    *   The set of positions and segments representing the city.
    * @return
    *   `true` if the city is completed, `false` otherwise.
    */
  def checkCityCompleted(map: CarcassonneBoard, connectedFeatures: Set[(Position, TileSegment)]): Boolean =
    val tiles = map.getTileMap.get.map((position, gametile) => stringifyTile(position, gametile)).mkString("\n")
    val engine: Term => LazyList[Term] = mkPrologEngine(
      tiles +
        """
          neighbor(nw, [(0, -1, s), (-1, 0, e)]).
          neighbor(n, [(0, -1, s)]).
          neighbor(ne, [(0, -1, s), (1, 0, w)]).
          neighbor(w, [(-1,  0, e)]).
          neighbor(e, [(1,  0, w)]).
          neighbor(sw, [(0,  1, n), (-1,  0, e)]).
          neighbor(s, [(0,  1, n)]).
          neighbor(se, [(0,  1, n), (1,  0, w)]).
          neighbor(c, []).
          get_value_tile(Segment, [(Segment, Value) | _], Value).
          get_value_tile(Segment, [_ | Tail], Value) :- get_value_tile(Segment, Tail, Value).
          is_city_segment(X, Y, Segment) :- tile(X, Y, TerrainMap), get_value_tile(Segment, TerrainMap, city).
          city_completed([(X, Y, Segment) | []]) :- check_current_tile([(X, Y, Segment)]), !.
          city_completed([(X, Y, Segment) | Tail]) :- Tail = [_|_], check_current_tile([(X, Y, Segment)]), city_completed(Tail), !.
          check_current_tile([(X, Y, Segment)]) :- neighbor(Segment, ReturnedNeighbor), check_neighbor(X, Y, ReturnedNeighbor).
          check_neighbor(X, Y, []) :- !.
          check_neighbor(X, Y, [(DX, DY, NSegment) | []]) :- check_current_neighbor(X, Y, [(DX, DY, NSegment)]), !.
          check_neighbor(X, Y, [(DX, DY, NSegment) | Tail]) :- Tail = [_|_], check_current_neighbor(X, Y, [(DX, DY, NSegment)]), check_neighbor(X, Y, Tail), !.
          check_current_neighbor(X, Y, [(DX, DY, NSegment)]) :- NX is X + DX, NY is Y + DY, is_city_segment(NX, NY, NSegment).
        """
    )
    engine(stringifyConnectedFeatures("city", connectedFeatures)).nonEmpty

  /** Checks if a road feature is completed in the Carcassonne game board.
    *
    * @param map
    *   The `CarcassonneBoard` containing the game state.
    * @param connectedFeatures
    *   The set of positions and segments representing the road.
    * @return
    *   `true` if the road is completed, `false` otherwise.
    */
  def checkRoadCompleted(map: CarcassonneBoard, connectedFeatures: Set[(Position, TileSegment)]): Boolean =
    val tiles = map.getTileMap.get.map((position, gametile) => stringifyTile(position, gametile)).mkString("\n")
    val engine: Term => LazyList[Term] = mkPrologEngine(
      tiles +
        """
          neighbor(n, [(0, -1, s)]).
          neighbor(w, [(-1,  0, e)]).
          neighbor(e, [(1,  0, w)]).
          neighbor(s, [(0,  1, n)]).
          neighbor(c, []).
          get_value_tile(Segment, [(Segment, Value) | _], Value).
          get_value_tile(Segment, [_ | Tail], Value) :- get_value_tile(Segment, Tail, Value).
          is_road_segment(X, Y, Segment) :- tile(X, Y, TerrainMap), get_value_tile(Segment, TerrainMap, road).
          road_completed([(X, Y, Segment) | []]) :- check_current_tile([(X, Y, Segment)]), true.
          road_completed([(X, Y, Segment) | Tail]) :- Tail = [_|_], check_current_tile([(X, Y, Segment)]), road_completed(Tail), !.
          check_current_tile([(X, Y, Segment)]) :- neighbor(Segment, ReturnedNeighbor), check_neighbor(X, Y, ReturnedNeighbor).
          check_neighbor(X, Y, []) :- !.
          check_neighbor(X, Y, [(DX, DY, NSegment) | []]) :- check_current_neighbor(X, Y, [(DX, DY, NSegment)]), !.
          check_neighbor(X, Y, [(DX, DY, NSegment) | Tail]) :- Tail = [_|_], check_current_neighbor(X, Y, [(DX, DY, NSegment)]), check_neighbor(X, Y, Tail), !.
          check_current_neighbor(X, Y, [(DX, DY, NSegment)]) :- NX is X + DX, NY is Y + DY, is_road_segment(NX, NY, NSegment).
        """
    )
    engine(stringifyConnectedFeatures("road", connectedFeatures)).nonEmpty

  /** Checks if a monastery feature is completed in the Carcassonne game board.
    *
    * @param map
    *   The `CarcassonneBoard` containing the game state.
    * @param connectedFeatures
    *   The set of positions and segments representing the monastery.
    * @return
    *   `true` if the monastery is completed, `false` otherwise.
    */
  def checkMonasteryCompleted(map: CarcassonneBoard, connectedFeatures: Set[(Position, TileSegment)]): Boolean =
    val tiles = map.getTileMap.get.map((position, gametile) => stringifyTile(position, gametile)).mkString("\n")
    val engine: Term => LazyList[Term] = mkPrologEngine(
      tiles +
        """
          neighbor(c, [(-1, -1), (0, -1), (1, -1), (-1, 0), (1, 0), (-1, 1), (0, 1), (1, 1)]).
          get_value_tile(Segment, [(Segment, Value) | _], Value).
          get_value_tile(Segment, [_ | Tail], Value) :- get_value_tile(Segment, Tail, Value).
          is_tile_present(X, Y) :- tile(X, Y, TerrainMap).
          monastery_completed([(X, Y, Segment)]) :- check_current_tile(X, Y, Segment), !.
          check_current_tile(X, Y, Segment) :- neighbor(Segment, ReturnedNeighbor), check_neighbor(X, Y, ReturnedNeighbor).
          check_neighbor(X, Y, [(DX, DY) | []]) :- check_current_neighbor(X, Y, [(DX, DY)]), !.
          check_neighbor(X, Y, [(DX, DY) | Tail]) :- Tail = [_|_], check_current_neighbor(X, Y, [(DX, DY)]), check_neighbor(X, Y, Tail), !.
          check_current_neighbor(X, Y, [(DX, DY)]) :- NX is X + DX, NY is Y + DY, is_tile_present(NX, NY).
        """
    )
    engine(stringifyConnectedFeatures("monastery", connectedFeatures)).nonEmpty
