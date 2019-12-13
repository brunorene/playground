package adventofcode.day7

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import com.marcinmoskala.math.permutations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day7star1()
    day7star2()
}

private suspend fun channels(order: List<Long>) = listOf(
        Channel<Long>(10000).apply { send(order[0]); send(0) },
        Channel<Long>(10000).apply { send(order[1]) },
        Channel<Long>(10000).apply { send(order[2]) },
        Channel<Long>(10000).apply { send(order[3]) },
        Channel<Long>(10000).apply { send(order[4]) }
)

fun day7star1() {
    val originalCode = readProgram(File("day7.txt"))
    println((0L..4L).toList().permutations().map { order ->
        var result = -1L
        runBlocking {
            val channels = channels(order)
            GlobalScope.async {
                computer(originalCode.toMutableMap(), channels[0]) { out -> channels[1].send(out) }
                computer(originalCode.toMutableMap(), channels[1]) { out -> channels[2].send(out) }
                computer(originalCode.toMutableMap(), channels[2]) { out -> channels[3].send(out) }
                computer(originalCode.toMutableMap(), channels[3]) { out -> channels[4].send(out) }
                computer(originalCode.toMutableMap(), channels[4]) { out -> result = out }
                result
            }
        }
    }.map { result -> runBlocking { result.await() } }.max())
}

fun day7star2() {
    val originalCode = readProgram(File("day7.txt"))
    println((5L..9L).toList().permutations().map { order ->
        var result = -1L
        runBlocking {
            val channels = channels(order)
            with(GlobalScope) {
                launch { computer(originalCode.toMutableMap(), channels[0]) { channels[1].send(it) } }
                launch { computer(originalCode.toMutableMap(), channels[1]) { channels[2].send(it) } }
                launch { computer(originalCode.toMutableMap(), channels[2]) { channels[3].send(it) } }
                launch { computer(originalCode.toMutableMap(), channels[3]) { channels[4].send(it) } }
                async {
                    computer(originalCode.toMutableMap(), channels[4]) { channels[0].send(it); result = it }
                    result
                }
            }
        }
    }.map { result -> runBlocking { result.await() } }.max())
}
