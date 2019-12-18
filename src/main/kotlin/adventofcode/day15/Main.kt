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

sealed class Tile(val char: Char)
object Wall : Tile('#')
object Empty : Tile('.')
object Oxygen : Tile('X')
object Unknown : Tile(' ')

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

object Hull {
    private val data = mutableMapOf<Point, Tile>()

    private fun tilePlace(p: Point) = data[p] ?: Unknown

    private fun allVisited(now: Point): Set<Direction> {
        val directions = mutableSetOf<Direction>()
        if (data.containsKey(now.nextPosition(North)))
            directions.add(North)
        if (data.containsKey(now.nextPosition(South)))
            directions.add(South)
        if (data.containsKey(now.nextPosition(West)))
            directions.add(West)
        if (data.containsKey(now.nextPosition(East)))
            directions.add(East)
        return directions
    }

    fun set(p: Point, t: Tile) {
        data[p] = t
    }

    fun draw() {
        ((data.keys.map { it.y }.min() ?: 0) until (data.keys.map { it.y }.max() ?: 0)).forEach { y ->
            ((data.keys.map { it.x }.min() ?: 0) until (data.keys.map { it.x }.max() ?: 0)).forEach { x ->
                print(tilePlace(Point(x, y)).char)
            }
            println()
        }
    }

    fun randomDirection(now: Point): Direction {
        var direction = direction(Random.nextLong(4) + 1L)
        if (allVisited(now).size < 4)
            while (allVisited(now).contains(direction)) {
                direction = direction(Random.nextLong(4) + 1L)
            }
        else
            direction = direction(Random.nextLong(4) + 1L)
        return direction
    }
}

fun day15star1() {
    val memory = readProgram(File("day15.txt"))
    val channel = Channel<Long>(1000)
    var currentPosition = Point()
    runBlocking {
        var direction = Hull.randomDirection(currentPosition)
        channel.send(direction.id)
        computer(memory, channel) {
            when (tile(it)) {
                Wall -> {
                    val wallPos = currentPosition.nextPosition(direction)
                    Hull.set(wallPos, Wall)
                    direction = Hull.randomDirection(currentPosition)
                }
                Empty -> {
                    currentPosition = currentPosition.nextPosition(direction)
                    Hull.set(currentPosition, Empty)
                    direction = Hull.randomDirection(currentPosition)
                }
                Oxygen -> {
                    currentPosition = currentPosition.nextPosition(direction)
                    Hull.set(currentPosition, Oxygen)
                    println("Found Oxygen Station on $currentPosition")
                    Hull.draw()
                    exitProcess(0)
                }
            }
            channel.send(direction.id)
        }
    }
}

fun day15star2() {
}
