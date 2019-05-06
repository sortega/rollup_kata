package rollup

import scalaz.Scalaz._

final class RollupAggregator(dimensions: List[String]) {
  def rollup(metrics: Metrics): Metrics = ???
}
