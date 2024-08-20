package observers

import mainApplication.Position
import scalafx.scene.layout.Region

trait SubjectGameView[S] {
  this: S =>
  private var observers: List[ObserverGameView[S]] = Nil

  def addObserver(observer: ObserverGameView[S]): Unit = observers = observer :: observers

  def notifyTilePlacementAttempt(position: Position): Unit = observers.foreach(_.receiveTilePlacementAttempt(position))
}
