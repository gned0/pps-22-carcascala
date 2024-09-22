package carcassonne.model.scalaprolog

import alice.tuprolog.*
object ScalaPrologEngine:
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[",",","]")

  def extractTerm(t:Term, i:Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  def mkPrologEngine(clauses: String*): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(clauses mkString " "))
    goal => new Iterable[Term]{
      override def iterator: Iterator[Term] = new Iterator[Term] {
        var solution: SolveInfo = engine.solve(goal)
        
        def moveToNextSolution(): Unit =
          if solution.hasOpenAlternatives then
            solution = engine.solveNext()
          else
            solution = null

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