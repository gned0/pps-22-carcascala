trait Observer[S] {
  def receiveUpdate(subject: S): Unit;
}
