package observers

import mainApplication._

trait ObserverGameMap[S] {

  def isTilePlaced(isTilePlaced: Boolean, 
                   tiles: Option[Map[Position, GameTile]], 
                   position: Position): Unit
}
