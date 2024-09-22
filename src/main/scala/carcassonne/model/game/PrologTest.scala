import alice.tuprolog.*

object Scala2P:
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[",",","]")

  def extractTerm(t:Term, i:Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  def mkPrologEngine(clauses: String*): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(clauses mkString " "))
    goal => new Iterable[Term]{
      override def iterator = new Iterator[Term] {
        var solution = engine.solve(goal);

        // Helper method to move to the next solution safely
        def moveToNextSolution(): Unit =
          if solution.hasOpenAlternatives then
            solution = engine.solveNext()
          else
            solution = null // Mark the end of solutions

        override def hasNext: Boolean =
          solution != null && solution.isSuccess

        override def next(): Term =
          if solution != null && solution.isSuccess then
            val result = solution.getSolution
            moveToNextSolution() // Move to next after retrieving current
            result
          else throw new NoSuchElementException("No more solutions")
      }
    }.to(LazyList)

object TryScala2P extends App:
  import Scala2P.{*, given}

  val engine: Term => LazyList[Term] = mkPrologEngine("""
    tile(10, 10, [(nw, field), (n, field), (ne, field), (w, field), (c, field), (e, city), (sw, field), (s, city), (se, city)]).
    tile(10, 11, [(nw, field), (n, city), (ne, field), (w, field), (c, field), (e, field), (sw, field), (s, field), (se, field)]).
    tile(11, 10, [(nw, field), (n, field), (ne, field), (w, city), (c, field), (e, field), (sw, field), (s, city), (se, field)]).
    tile(11, 11, [(nw, field), (n, city), (ne, field), (w, city), (c, field), (e, field), (sw, field), (s, field), (se, field)]).
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

//  engine("city_completed([(10, 10, s), (10, 10, se), (10, 10, e), (10, 11, n), (11, 10, w)])") foreach (println(_))
  val solution = engine("city_completed([(11, 10, s), (11, 11, n)])")
  if solution.isEmpty then
    println("no")
  else
    println("yes")

