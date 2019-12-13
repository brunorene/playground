package adventofcode.day9

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day9star1()
    day9star2()
}

fun day9star1() {
    runBlocking {
        computer(readProgram(File("day9.txt")), Channel<Long>(1).apply { send(1L) }) { println(it) }
    }
}


fun day9star2() {
    runBlocking {
        computer(readProgram(File("day9.txt")), Channel<Long>(1).apply { send(2L) }) { println(it) }
    }
}
