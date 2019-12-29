package adventofcode.day19

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day19star1()
    day19star2()
}

fun day19star1() {
    val memory = readProgram(File("day19.txt"))
    runBlocking {
        println((0L..49L).flatMap { x ->
            (0L..49L).map { y ->
                async {
                    computer(memory.toMutableMap(), Channel<Pair<Unit, Long>>(2)
                            .apply { send(Unit to x); send(Unit to y) }) { it }.first()
                }
            }
        }.map { it.await().first }.sum())
    }
}

fun day19star2() {
    val memory = readProgram(File("day19.txt"))
    runBlocking {
        println((0L..49L).flatMap { x ->
            (0L..49L).map { y ->
                async {
                    computer(memory.toMutableMap(), Channel<Pair<Unit, Long>>(2).apply { send(Unit to x); send(Unit to y) }) { it }.first()
                }
            }
        }.map { it.await().first }.sum())
    }
}
