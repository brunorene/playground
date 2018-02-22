package coroutines

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

val deferred = (1..1_000_000).map { n ->
    async {
        n
    }
}

fun runB() = runBlocking {
    val sum = deferred.sumBy { it.await() }
    println("Sum: $sum")
}
