package adventofcode.day15

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import kotlin.system.exitProcess

typealias Depth = Int

val hasOxygen = mutableSetOf<Point>()

fun fillWithO2(center: Point, minutes: Int): Int = allDirections
        .map { center.nextPosition(it) }
        .map { it to hull[it] }
        .filter { it.second?.tile != Wall && !hasOxygen.contains(it.first) }
        .let { next ->
            next.map {
                hasOxygen.add(center)
                fillWithO2(it.first, minutes + 1)
            }.max() ?: minutes
        }

val day15star2 = { oxygenData: Pair<Point, PositionData>? ->
    if (oxygenData != null) {
        println("oxygen station $oxygenData")
        println("minutes ${fillWithO2(oxygenData.first, 0)}")
    }
    exitProcess(0)
}

fun main() {
    day15star1(day15star2)
}

data class Point(val x: Long = 0, val y: Long = 0) {

    fun nextPosition(dir: Direction) = when (dir) {
        North -> Point(x, y - 1)
        South -> Point(x, y + 1)
        West -> Point(x - 1, y)
        East -> Point(x + 1, y)
    }
}

sealed class Tile(val char: Char) {
    override fun toString(): String = javaClass.simpleName
}

object Wall : Tile('#')
object Empty : Tile('.')
object Oxygen : Tile('X')
object Start : Tile('@')

fun tile(id: Long) = when (id) {
    0L -> Wall
    1L -> Empty
    2L -> Oxygen
    else -> throw RuntimeException("unknown tile")
}

sealed class Direction(val id: Long) {
    override fun toString(): String = javaClass.simpleName

    abstract fun reverse(): Direction
}

object North : Direction(1) {
    override fun reverse() = South
}

object South : Direction(2) {
    override fun reverse() = North
}

object West : Direction(3) {
    override fun reverse() = East
}

object East : Direction(4) {
    override fun reverse() = West
}

private val allDirections = listOf(North, South, East, West)

data class PositionData(
        val direction: Direction,
        val depth: Depth,
        val tile: Tile
)

private val hull = mutableMapOf<Point, PositionData>()
fun MutableMap<Point, PositionData>.depth(p: Point) = get(p)?.depth ?: 0

fun MutableMap<Point, PositionData>.draw(p: Pair<Point, PositionData>?) {
    File("hull.draw").bufferedWriter().use { writer ->
        if (p != null) {
            writer.write(p.toString())
            writer.newLine()
        }
        ((keys.map { it.y }.min() ?: 0)..(keys.map { it.y }.max() ?: 0)).forEach { y ->
            ((keys.map { it.x }.min() ?: 0)..(keys.map { it.x }.max() ?: 0)).forEach { x ->
                writer.write((hull[Point(x, y)]?.tile?.char ?: ' ').toInt())
            }
            writer.newLine()
        }
    }
}

fun MutableMap<Point, PositionData>.allFound() = filter { it.value.tile in listOf(Empty, Start) }
        .all { data ->
            allDirections.all { direction -> this.containsKey(data.key.nextPosition(direction)) }
        }

fun day15star1(day2: (Pair<Point, PositionData>?) -> Unit) {
    runBlocking {
        val memory = readProgram(File("day15.txt"))
        val channel = Channel<Pair<Direction, Long>>(1024)
        var currentPoint = Point()
        var oxygenPoint: Pair<Point, PositionData>? = null
        hull[Point()] = PositionData(South, 0, Start)
        val queue = LinkedList<Direction>().apply { addAll(allDirections) }
        queue.pop().also { channel.send(it to it.id) }
        computer(memory, channel) { (tileCode, stack) ->
            val nextDirection = stack.pop()
            val nextPoint = currentPoint.nextPosition(nextDirection)
            when (tile(tileCode)) {
                Wall -> {
                    hull[nextPoint] = PositionData(
                            nextDirection,
                            hull.depth(currentPoint) + 1,
                            Wall
                    )
                }
                Empty, Oxygen -> {
                    val data = PositionData(
                            nextDirection,
                            hull.depth(currentPoint) + 1,
                            tile(tileCode)
                    )
                    hull.putIfAbsent(nextPoint, data)
                    if (tile(tileCode) == Oxygen) oxygenPoint = nextPoint to data
                    currentPoint = nextPoint
                    queue.clear()
                    queue.addAll(allDirections
                            .filter { !hull.containsKey(currentPoint.nextPosition(it)) && it != nextDirection.reverse() })
                }
            }
            if (queue.isEmpty()) {
                val randomDirection = allDirections
                        .filter { hull[currentPoint.nextPosition(it)]?.tile != Wall }
                        .random()
                channel.send(randomDirection to randomDirection.id)
            } else {
                queue.pop().also { channel.send(it to it.id) }
            }
            if (hull.allFound()) {
                hull.draw(oxygenPoint)
                day2(oxygenPoint)
            }
        }
    }
}