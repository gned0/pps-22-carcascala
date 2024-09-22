package carcassonne.model.scalaprolog

import alice.tuprolog.Term
import carcassonne.model.board.CarcassonneBoard
import carcassonne.util.ScalaPrologUtils.*
import carcassonne.model.scalaprolog.ScalaPrologEngine.{*, given}
import carcassonne.model.tile.TileSegment
import carcassonne.util.Position

class PrologProcessing:
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
            get_value_tile(Segment, [(Segment, Value) | _], Value).
            get_value_tile(Segment, [_ | Tail], Value) :- get_value_tile(Segment, Tail, Value).
            is_city_segment(X, Y, Segment) :- tile(X, Y, TerrainMap), get_value_tile(Segment, TerrainMap, city).
            city_completed([(X, Y, Segment) | []]) :- check_current_tile([(X, Y, Segment)]), !.
            city_completed([(X, Y, Segment) | Tail]) :- Tail = [_|_], check_current_tile([(X, Y, Segment)]), city_completed(Tail), !.
            check_current_tile([(X, Y, Segment)]) :- neighbor(Segment, ReturnedNeighbor), check_neighbor(X, Y, ReturnedNeighbor).
            check_neighbor(X, Y, [(DX, DY, NSegment) | []]) :- check_current_neighbor(X, Y, [(DX, DY, NSegment)]), !.
            check_neighbor(X, Y, [(DX, DY, NSegment) | Tail]) :- Tail = [_|_], check_current_neighbor(X, Y, [(DX, DY, NSegment)]), check_neighbor(X, Y, Tail), !.
            check_current_neighbor(X, Y, [(DX, DY, NSegment)]) :- NX is X + DX, NY is Y + DY, is_city_segment(NX, NY, NSegment).
          """)
    val solution = engine(stringifyConnectedFeatures(s"city", connectedFeatures))
    if solution.isEmpty then
      false
    else
      true

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
        """)
    val solution = engine(stringifyConnectedFeatures(s"road", connectedFeatures))
    if solution.isEmpty then
      false
    else
      true


