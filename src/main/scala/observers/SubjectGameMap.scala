package observers

import mainApplication._

trait SubjectGameMap[S] {
  this: S =>
  private var observers: List[ObserverGameMap[S]] = Nil

  def addObserver(observer: ObserverGameMap[S]): Unit = observers = observer :: observers

  def getObservers: List[ObserverGameMap[S]] = observers

  def notifyIsTilePlaced(isTilePlaced: Boolean,
                         tiles: Option[Map[Position, GameTile]],
                         position: Position): Unit =
    observers.foreach(_.isTilePlaced(isTilePlaced, tiles, position))

}
