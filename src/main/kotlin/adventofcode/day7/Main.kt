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
        Channel<Pair<Unit, Long>>(10000).apply { send(Unit to order[0]); send(Unit to 0) },
        Channel<Pair<Unit, Long>>(10000).apply { send(Unit to order[1]) },
        Channel<Pair<Unit, Long>>(10000).apply { send(Unit to order[2]) },
        Channel<Pair<Unit, Long>>(10000).apply { send(Unit to order[3]) },
        Channel<Pair<Unit, Long>>(10000).apply { send(Unit to order[4]) }
)

fun day7star1() {
    val originalCode = readProgram(File("day7.txt"))
    println((0L..4L).toList().permutations().map { order ->
        var result = -1L
        runBlocking {
            val channels = channels(order)
            GlobalScope.async {
                computer(originalCode.toMutableMap(), channels[0]) { (out, _) -> channels[1].send(Unit to out) }
                computer(originalCode.toMutableMap(), channels[1]) { (out, _) -> channels[2].send(Unit to out) }
                computer(originalCode.toMutableMap(), channels[2]) { (out, _) -> channels[3].send(Unit to out) }
                computer(originalCode.toMutableMap(), channels[3]) { (out, _) -> channels[4].send(Unit to out) }
                computer(originalCode.toMutableMap(), channels[4]) { (out, _) -> result = out }
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
                launch { computer(originalCode.toMutableMap(), channels[0]) { (out, _) -> channels[1].send(Unit to out) } }
                launch { computer(originalCode.toMutableMap(), channels[1]) { (out, _) -> channels[2].send(Unit to out) } }
                launch { computer(originalCode.toMutableMap(), channels[2]) { (out, _) -> channels[3].send(Unit to out) } }
                launch { computer(originalCode.toMutableMap(), channels[3]) { (out, _) -> channels[4].send(Unit to out) } }
                async {
                    computer(originalCode.toMutableMap(), channels[4]) { (out, _) -> channels[0].send(Unit to out); result = out }
                    result
                }
            }
        }
    }.map { result -> runBlocking { result.await() } }.max())
}
