package observers

import mainApplication.Position
import scalafx.scene.layout.Region

trait Subject[S] {
  this: S =>
  private var observers: List[Observer[S]] = Nil

  def addObserver(observer: Observer[S]): Unit = observers = observer :: observers

  def notifyObservers(): Unit = observers.foreach(_.receiveUpdate(this))

  def notifyGetTileMap(position: Position, region: Region): Unit = observers.foreach(_.receiveModelMapTile(position, region))
}
