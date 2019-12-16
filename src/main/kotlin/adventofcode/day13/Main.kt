package adventofcode.day13

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day13star1()
    day13star2()
}

sealed class Tile(val id: Long)
object Empty : Tile(0)
object Wall : Tile(1)
object Block : Tile(2)
object Paddle : Tile(3)
object Ball : Tile(4)

fun tile(id: Long) = when (id) {
    0L -> Empty
    1L -> Wall
    2L -> Block
    3L -> Paddle
    4L -> Ball
    else -> throw RuntimeException("unknown tile")
}

data class Point(val x: Long, val y: Long)

var screen = mutableMapOf<Point, Tile>()

fun day13star1() {
    runBlocking {
        val memory = readProgram(File("day13.txt"))
        var buffer = listOf<Long>()
        computer(memory) {
            buffer += it
            if (buffer.size == 3) {
                screen[Point(buffer[0], buffer[1])] = tile(buffer[2])
                buffer = listOf()
            }
        }
        println(screen.values.count { it == Block })
    }
}

fun day13star2() {
    runBlocking {
        val memory = readProgram(File("day13.txt"))
        memory[0] = 2
        var buffer = listOf<Long>()
        computer(memory) {
            buffer += it
            if (buffer.size == 3) {
                if (buffer[0] == -1L && buffer[1] == 0L) { //score

                } else {
                    screen[Point(buffer[0], buffer[1])] = tile(buffer[2])
                }
                buffer = listOf()
            }
        }
        println(screen.values.count { it == Block })
    }
}
