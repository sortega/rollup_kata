package rollup
import java.time.Instant

final case class Key(name: String, dimensions: Map[String, String], window: Instant)

