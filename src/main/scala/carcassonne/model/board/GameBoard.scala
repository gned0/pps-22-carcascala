package carcassonne.model.board

import carcassonne.util.Position

/**
 * Represents a board table that manages positions and elements.
 *
 * @tparam T The type of elements on the board.
 */
trait GameBoard[T]:
  private var elements: Map[Position, T] = Map.empty

  /**
   * Places an element on the board at the specified position.
   *
   * @param elem The element to place.
   * @param position The position where the element should be placed.
   * @throws IllegalArgumentException if an element is already placed at the position.
   */
  def placeElement(elem: T, position: Position): Unit =
    if elements.contains(position) then
      throw IllegalArgumentException(s"Element already placed at position $position")
    elements += (position -> elem)

  /**
   * Retrieves the element at the specified position, if any.
   *
   * @param position The position to retrieve the element from.
   * @return An `Option` containing the element if one exists at the position, or `None` otherwise.
   */
  def getElement(position: Position): Option[T] =
    elements.get(position)

  /**
   * Retrieves the entire map of placed elements.
   *
   * @return An `Option` containing a `Map` of `Position` to `T`.
   */
  def getElementMap: Option[Map[Position, T]] = Option(this.elements)

  /**
   * Removes an element from the board at the specified position.
   *
   * @param position The position to remove the element from.
   */
  def removeElement(position: Position): Unit =
    elements -= position

