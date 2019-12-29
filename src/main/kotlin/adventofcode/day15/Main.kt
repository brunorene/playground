package adventofcode.day15

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

typealias Depth = Int

fun main() {
    day15star1()
    day15star2()
}

data class Point(val x: Long = 0, val y: Long = 0) {

    fun nextPosition(dir: Direction) = when (dir) {
        North -> Point(x, y - 1)
        South -> Point(x, y + 1)
        West -> Point(x - 1, y)
        East -> Point(x + 1, y)
    }

    fun neighbours() = listOf(North, South, East, West)
            .filter { hull[nextPosition(it)]?.second !in listOf(Wall, DeadEnd) }
            .map { it to nextPosition(it) }
}

sealed class Tile(val char: Char) {
    override fun toString(): String = javaClass.simpleName
}

object Wall : Tile('#')
object Empty : Tile('.')
object Oxygen : Tile('X')
object Unknown : Tile(' ')
object DeadEnd : Tile('D')

fun tile(id: Long) = when (id) {
    0L -> Wall
    1L -> Empty
    2L -> Oxygen
    else -> throw RuntimeException("unknown tile")
}

sealed class Direction(val id: Long) {
    override fun toString(): String = javaClass.simpleName
}

object North : Direction(1)
object South : Direction(2)
object West : Direction(3)
object East : Direction(4)

private val hull = mutableMapOf<Point, Pair<Depth, Tile>>()

fun Point.isDeadEnd() = neighbours().count { hull[it.second]?.second in setOf(DeadEnd, Wall) } == 3

fun MutableMap<Point, Pair<Depth, Tile>>.draw() {
    File("hull.draw").bufferedWriter().use { writer ->
        ((keys.map { it.y }.min() ?: 0)..(keys.map { it.y }.max() ?: 0)).forEach { y ->
            ((keys.map { it.x }.min() ?: 0)..(keys.map { it.x }.max() ?: 0)).forEach { x ->
                writer.write(hull[Point(x, y)]?.second?.char.toString())
            }
            writer.newLine()
        }
    }
}

fun Pair<Direction, Point>.backtrack() = when (first) {
    North -> Point(second.x, second.y + 1)
    South -> Point(second.x, second.y - 1)
    West -> Point(second.x + 1, second.y)
    East -> Point(second.x - 1, second.y)
}

fun Pair<Direction, Point>.neighbours() = listOf(North, South, East, West)
        .filter { first != it && hull[second.nextPosition(it)]?.second !in listOf(Wall, DeadEnd) }
        .map { it to second.nextPosition(it) }

fun day15star1() {
    runBlocking {
        val memory = readProgram(File("day15.txt"))
        hull[Point()] = 0 to Empty
        val channel = Channel<Pair<Pair<Direction, Point>, Long>>(1024)
        Point().neighbours().forEach { channel.send(it to it.first.id) }
        computer(memory, channel) { (tileCode, stack) ->
            val nextPosData = stack.pop()
            print(nextPosData)
            if (hull[nextPosData.second]?.second == Unknown)
                when (tile(tileCode)) {
                    Wall -> {
                        println(" Wall")
                        hull[nextPosData.second] = -1 to Wall
                        val previous = nextPosData.backtrack()
                        if (previous.isDeadEnd()) {
                            val backtrack = previous.neighbours().first()
                            hull[previous] = -1 to DeadEnd
                            channel.send(backtrack to backtrack.first.id)
                        }
                    }
                    Empty -> {
                        println(" Empty")
                        hull[nextPosData.second] = ((hull[nextPosData.backtrack()]?.first ?: 0) + 1) to Empty
                        nextPosData.neighbours()
                                .filter { hull[it.second]?.second == Unknown }
                                .map { neighbour -> channel.send(neighbour to neighbour.first.id) }
                    }
                    Oxygen -> {
                        println(" Oxygen")
                        hull[nextPosData.second] = ((hull[nextPosData.backtrack()]?.first ?: 0) + 1) to Oxygen
                        println("Found Oxygen Station on ${hull[nextPosData.second]}")
                    }
                }
        }
    }
    hull.draw()
}

fun day15star2() {
    println("Ola 2")
}
