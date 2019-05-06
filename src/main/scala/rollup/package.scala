package object rollup {

  type Metrics = Map[Key, Int]

  object Metrics {
    val Empty: Metrics = Map.empty
  }
}
