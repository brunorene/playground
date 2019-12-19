package adventofcode.day15

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import kotlin.system.exitProcess

fun main() {
    day15star1()
    day15star2()
}

class Point(val x: Long = 0, val y: Long = 0, private val depth: Int = 0) {
    fun nextPosition(dir: Direction) = when (dir) {
        North -> Point(x, y - 1, depth + 1)
        South -> Point(x, y + 1, depth + 1)
        West -> Point(x - 1, y, depth + 1)
        East -> Point(x + 1, y, depth + 1)
    }

    fun nextDirection(p: Point) = when {
        x == p.x && y < p.y -> North
        x == p.x && y > p.y -> South
        x < p.x && y == p.y -> East
        x > p.x && y == p.y -> West
        else -> throw RuntimeException("it is the same node!")
    }

    fun neighbours() = listOf(North, South, East, West).map { nextPosition(it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "Point(x=$x, y=$y, depth=$depth)"
    }


}

sealed class Tile(val char: Char)
object Wall : Tile('#')
object Empty : Tile('.')
object Oxygen : Tile('X')
object Unknown : Tile(' ')

fun tile(id: Long) = when (id) {
    0L -> Wall
    1L -> Empty
    2L -> Oxygen
    else -> throw RuntimeException("unknown tile")
}

sealed class Direction(val id: Long)
object North : Direction(1)
object South : Direction(2)
object West : Direction(3)
object East : Direction(4)

object Hull {
    private val data = mutableMapOf<Point, Tile>()

    fun tilePlace(p: Point) = data[p] ?: Unknown

    fun set(p: Point, t: Tile) {
        data[p] = t
    }

    fun draw() {
        File("hull.draw").bufferedWriter().use { writer ->
            ((data.keys.map { it.y }.min() ?: 0)..(data.keys.map { it.y }.max() ?: 0)).forEach { y ->
                ((data.keys.map { it.x }.min() ?: 0)..(data.keys.map { it.x }.max() ?: 0)).forEach { x ->
                    writer.write(tilePlace(Point(x, y)).char.toString())
                }
                writer.newLine()
            }
        }
    }

    fun randomDirection(now: Point): Direction {
        val possibleDirections = listOf(North, South, East, West)
                .filter { tilePlace(now.nextPosition(it)) != Wall }
        val unknownDirection = possibleDirections
                .filter { tilePlace(now.nextPosition(it)) == Unknown }
        return if (unknownDirection.isNotEmpty()) unknownDirection.random() else possibleDirections.random()
    }
}

fun day15star1() {
    val memory = readProgram(File("day15.txt"))
    val channel = Channel<Long>(1000)
    var currentPosition = Point()
    var toVisit = LinkedList<Point>()
    Hull.set(currentPosition, Empty)
    currentPosition.neighbours().forEach { toVisit.push(it) }
    runBlocking {
        var visiting = toVisit.pop()
        var direction = currentPosition.nextDirection(visiting)
        channel.send(direction.id)
        computer(memory, channel) { tile ->
            when (tile(tile)) {
                Wall -> Hull.set(visiting, Wall)
                Empty -> {
                    currentPosition = visiting
                    Hull.set(visiting, Empty)
                    currentPosition.neighbours()
                            .filter { Hull.tilePlace(it) == Unknown }
                            .forEach { toVisit.push(it) }
                }
                Oxygen -> {
                    Hull.set(visiting, Oxygen)
                    println("Found Oxygen Station on $visiting")
                    Hull.draw()
                    exitProcess(0)
                }
            }
            do {
                visiting = toVisit.pop()
                if (visiting.x != currentPosition.x && visiting.y != currentPosition.y)
                    println("impossible direction -> $visiting / $currentPosition")
            } while (visiting.x != currentPosition.x && visiting.y != currentPosition.y)
            println("Visiting $currentPosition -> $visiting")
            direction = currentPosition.nextDirection(visiting)
            println("Visiting $visiting -> $direction")
            channel.send(direction.id)
        }
    }
}

fun day15star2() {
}
