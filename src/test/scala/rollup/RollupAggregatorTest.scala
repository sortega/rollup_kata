package rollup

import java.time.Instant

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.PropertyChecks

final class RollupAggregatorTest
    extends FlatSpec
    with Matchers
    with TypeCheckedTripleEquals
    with PropertyChecks {
  private val window = Instant.ofEpochSecond(1556887620L)

  private val inputMetrics = Map(
    Key("metric1", Map("super-category" -> "animal", "category" -> "lion"), window) -> 1,
    Key("metric1", Map("super-category" -> "animal", "category" -> "hare"), window) -> 10,
    Key("metric1", Map("super-category" -> "plant", "category" -> "lettuce"), window) -> 100,
    Key("metric2", Map("super-category" -> "animal", "category" -> "lion"), window) -> 2,
    Key("unrelated", Map("some-dim"     -> "foo"), window) -> 42
  )

  "A rollup aggregator" should "do nothing when dimensions is empty" in {
    val aggregator = new RollupAggregator(Nil)

    aggregator.rollup(inputMetrics) should ===(inputMetrics)
  }

  it should "do a rollup on a single dimension" in {
    val aggregator = new RollupAggregator(List("category"))

    aggregator.rollup(inputMetrics) should ===(
      inputMetrics ++ Map(
        Key("metric1", Map("super-category" -> "animal"), window) -> 11,
        Key("metric1", Map("super-category" -> "plant"), window)  -> 100,
        Key("metric2", Map("super-category" -> "animal"), window) -> 2
      ))
  }

  it should "do a rollup aggregation on multiple dimensions" in {
    val aggregator = new RollupAggregator(List("super-category", "category"))

    aggregator.rollup(inputMetrics) should ===(
      inputMetrics ++ Map(
        // Aggregated over super-category
        Key("metric1", Map("super-category" -> "animal"), window) -> 11,
        Key("metric1", Map("super-category" -> "plant"), window)  -> 100,
        Key("metric2", Map("super-category" -> "animal"), window) -> 2,
        // Aggregated over category and super-category
        Key("metric1", Map.empty, window) -> 111,
        Key("metric2", Map.empty, window) -> 2
      ))
  }
}
