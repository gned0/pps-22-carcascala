package carcassonne.model.scalaprolog

import alice.tuprolog.*

/**
 * A utility object for interacting with a Prolog engine using the `tuProlog` library.
 * This object provides Scala-friendly functions for creating Prolog engines, extracting terms, 
 * and converting data to Prolog terms.
 */
object ScalaPrologEngine:

  /**
   * Implicit conversion from `String` to Prolog `Term`.
   * Allows easy creation of Prolog terms from strings.
   */
  given Conversion[String, Term] = Term.createTerm(_)

  /**
   * Implicit conversion from `Seq` to Prolog `Term`.
   * Converts a Scala sequence into a Prolog list term.
   */
  given Conversion[Seq[_], Term] = _.mkString("[",",","]")

  /**
   * Extracts a specific term from a Prolog structure at a given index.
   *
   * @param t The Prolog term, which should be a `Struct`.
   * @param i The index of the argument to extract.
   * @return  The extracted `Term` at the specified index.
   */
  def extractTerm(t:Term, i:Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  /**
   * Creates a Prolog engine and sets its theory (rules and facts) based on the provided clauses.
   * Returns a function that takes a goal as input and produces a lazy list of solutions.
   *
   * @param clauses The Prolog clauses (rules and facts) to be set as the theory.
   * @return        A function that, given a goal, returns a `LazyList` of solutions.
   */
  def mkPrologEngine(clauses: String*): Term => LazyList[Term] =
    val engine = Prolog()
    engine.setTheory(Theory(clauses mkString " "))
    goal => new Iterable[Term]{
      override def iterator: Iterator[Term] = new Iterator[Term] {
        var solution: SolveInfo = engine.solve(goal)

        /**
         * Moves to the next available solution if open alternatives exist.
         */
        def moveToNextSolution(): Unit =
          if solution.hasOpenAlternatives then
            solution = engine.solveNext()
          else
            solution = null

        /**
         * Checks if there are more solutions available.
         *
         * @return `true` if more solutions exist, `false` otherwise.
         */
        override def hasNext: Boolean =
          solution != null && solution.isSuccess

        /**
         * Retrieves the current solution and moves to the next one.
         *
         * @return The current `Term` solution.
         * @throws NoSuchElementException if no more solutions are available.
         */
        override def next(): Term =
          if solution != null && solution.isSuccess then
            val result = solution.getSolution
            moveToNextSolution() // Move to next after retrieving current
            result
          else throw new NoSuchElementException("No more solutions")
      }
    }.to(LazyList)
