package adventofcode.day13

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.Long.max

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
        val buffer = mutableListOf<Long>()
        computer(memory) {
            buffer.add(it)
            if (buffer.size == 3) {
                screen[Point(buffer[0], buffer[1])] = tile(buffer[2])
                buffer.clear()
            }
        }
        println(screen.values.count { it == Block })
    }
}

fun day13star2() {
    runBlocking {
        val memory = readProgram(File("day13.txt"))
        memory[0] = 2
        val buffer = mutableListOf<Long>()
        val channel = Channel<Long>(1000)
        var maxScore = 0L
        var paddleX = 0L
        computer(memory, channel) {
            buffer.add(it)
            if (buffer.size == 3) {
                if (buffer[0] == -1L && buffer[1] == 0L) { //score
                    maxScore = max(maxScore, buffer[2])
                } else {
                    val tile = tile(buffer[2])
                    if(tile == Paddle)
                        paddleX = buffer[0]
                    if(tile == Ball) {
                        if (buffer[0] < paddleX)
                            channel.send(-1L)
                        if (buffer[0] > paddleX)
                            channel.send(1L)
                        if (buffer[0] == paddleX)
                            channel.send(0)
                    }
                }
                buffer.clear()
            }
        }
        println(maxScore)
    }
}
