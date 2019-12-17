package adventofcode.day15

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

fun main() {
    day15star1()
    day15star2()
}

data class Point(val x: Long = 0, val y: Long = 0)

sealed class Tile(val id: Long)
object Wall : Tile(0)
object Empty : Tile(1)
object Oxygen : Tile(2)

fun tile(id: Long) = when (id) {
    0L -> Empty
    1L -> Wall
    2L -> Oxygen
    else -> throw RuntimeException("unknown tile")
}

sealed class Direction(val id: Long)
object North : Direction(1)
object South : Direction(2)
object West : Direction(3)
object East : Direction(4)

fun direction(id: Long) = when (id) {
    1L -> North
    2L -> South
    3L -> West
    4L -> East
    else -> throw RuntimeException("unknown direction")
}

fun Point.nextPosition(dir: Direction) = when (dir) {
    North -> Point(x, y - 1)
    South -> Point(x, y + 1)
    West -> Point(x - 1, y)
    East -> Point(x + 1, y)
}

fun randomDirection(exclude: Direction? = null): Direction {
    var direction = direction(Random.nextLong(4) + 1L)
    while (exclude != null && direction == exclude) {
        direction = direction(Random.nextLong(4) + 1L)
    }
    return direction
}

fun day15star1() {
    val memory = readProgram(File("day15.txt"))
    val channel = Channel<Long>(1000)
    val hull = mutableMapOf<Point, Tile>()
    var currentPosition = Point()
    runBlocking {
        var direction = randomDirection()
        channel.send(direction.id)
        computer(memory, channel) {
            when (tile(it)) {
                Wall -> {
                    val wallPos = currentPosition.nextPosition(direction)
                    hull[wallPos] = Wall
                    println("Found Wall on $wallPos")
                    direction = randomDirection(direction)
                }
                Empty -> {
                    currentPosition = currentPosition.nextPosition(direction)
                    hull[currentPosition] = Empty
                    println("Found Empty on $currentPosition")
                    direction = randomDirection()
                }
                Oxygen -> {
                    currentPosition = currentPosition.nextPosition(direction)
                    hull[currentPosition] = Oxygen
                    println("Found Oxygen Station on $currentPosition")
                    exitProcess(0)
                }
            }
            channel.send(direction.id)
        }
    }
}

fun day15star2() {
}
