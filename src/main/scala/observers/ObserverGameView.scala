package observers

import mainApplication.Position
import scalafx.scene.layout.Region

trait ObserverGameView[S] {
  def receiveTilePlacementAttempt(position: Position): Unit
}
