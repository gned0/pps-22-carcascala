class ObserverGameMapView extends GameMapView with Subject[GameMapView] {
  override def notifyTileClick(position: Position): Unit = 
    super.notifyTileClick(position)
    notifyObservers()
}
