package observers

import mainApplication.Position
import scalafx.scene.layout.Region

trait Observer[S] {
  def receiveUpdate(subject: S): Unit;

  def receiveModelMapTile(position: Position, region: Region): Unit;
}
