package adventofcode.day15

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File

fun main() {
    day15star1()
    day15star2()
}

class Point(val x: Long = 0, val y: Long = 0, private val depth: Int = 0) {

    private fun nextPosition(dir: Direction) = when (dir) {
        North -> Point(x, y - 1, depth + 1)
        South -> Point(x, y + 1, depth + 1)
        West -> Point(x - 1, y, depth + 1)
        East -> Point(x + 1, y, depth + 1)
    }

    fun neighbours() = listOf(North, South, East, West).map { it to nextPosition(it) }

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
}

suspend fun depthFirst(memory: MutableMap<Long, Long>, channel: Channel<Long>, current: Point, next: Point) {
    coroutineScope {
        computer(memory, channel) { tileCode ->
            when (tile(tileCode)) {
                Wall -> {
                    Hull.set(current, Wall)
                    coroutineContext.cancel()
                }
                Empty -> {
                    Hull.set(next, Empty)
                    next.neighbours()
                            .filter { Hull.tilePlace(it.second) == Unknown }
                            .map { neighbour ->
                                val newChannel = Channel<Long>(10).apply { send(neighbour.first.id) }
                                launch { depthFirst(memory.toMutableMap(), newChannel, next, neighbour.second) }
                            }
                }
                Oxygen -> {
                    Hull.set(next, Oxygen)
                    println("Found Oxygen Station on $next")
                    coroutineContext.cancel()
                }
                Unknown -> coroutineContext.cancel()
            }
        }
    }
}


fun day15star1() {
    val memory = readProgram(File("day15.txt"))
    val start = Point()
    Hull.set(start, Empty)
    val job = GlobalScope.launch {
        start.neighbours().map { neighbour ->
            val newChannel = Channel<Long>(10).apply { send(neighbour.first.id) }
            launch { depthFirst(memory.toMutableMap(), newChannel, start, neighbour.second) }
        }
    }
    runBlocking {
        job.join()
    }
    Hull.draw()
}

fun day15star2() {
}
