package adventofcode.day11

import adventofcode.day11.Colour.Black
import adventofcode.day11.Colour.White
import adventofcode.day11.Direction.*
import adventofcode.day11.Turn.TurnLeft
import adventofcode.day11.Turn.TurnRight
import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day11star1()
    day11star2()
}

data class Point(val x: Int = 0, val y: Int = 0)

enum class Direction { Up, Down, Left, Right }
enum class Turn {
    TurnLeft,
    TurnRight;

    companion object Init {
        fun get(id: Long) = when (id) {
            0L -> TurnLeft
            1L -> TurnRight
            else -> throw RuntimeException("Unknown Turn $id")
        }
    }
}

enum class Colour(val id: Long, val char: Char) {
    Black(0L, ' '),
    White(1L, '#');

    companion object Init {
        fun get(id: Long) = when (id) {
            0L -> Black
            1L -> White
            else -> throw RuntimeException("Unknown colour $id")
        }
    }
}


data class Robot(val place: Point = Point(), val direction: Direction = Up) {

    fun turn(turn: Turn) = Robot(
            place,
            when (turn) {
                TurnLeft -> when (direction) {
                    Up -> Left
                    Down -> Right
                    Left -> Down
                    Right -> Up
                }
                TurnRight -> when (direction) {
                    Up -> Right
                    Down -> Left
                    Right -> Down
                    Left -> Up
                }
            }
    )

    fun move() = Robot(
            when (direction) {
                Up -> Point(place.x, place.y - 1)
                Down -> Point(place.x, place.y + 1)
                Left -> Point(place.x - 1, place.y)
                Right -> Point(place.x + 1, place.y)
            },
            direction
    )
}

fun day11star1() {
    runBlocking {
        var isPaint = true
        var robot = Robot()
        val hull = mutableMapOf<Point, Colour>()
        val painted = mutableSetOf<Point>()
        val channel = Channel<Pair<Unit, Long>>(1)
        val hullColour = { p: Point -> hull[p] ?: Black }
        channel.send(Unit to Black.id)

        computer(
                readProgram(File("day11.txt")),
                channel
        ) { (out, _) ->
            if (isPaint) {
                if (hullColour(robot.place) != Colour.get(out))
                    painted.add(robot.place)
                hull[robot.place] = Colour.get(out)
                isPaint = false
            } else {
                robot = robot.turn(Turn.get(out)).move()
                channel.send(Unit to hullColour(robot.place).id)
                isPaint = true
            }
        }
        println(painted.size)
    }
}

fun day11star2() {
    runBlocking {
        var isPaint = true
        var robot = Robot()
        val hull = mutableMapOf<Point, Colour>()
        hull[Point()] = White
        val channel = Channel<Pair<Unit, Long>>(1)
        val hullColour = { p: Point -> hull[p] ?: Black }
        channel.send(Unit to hullColour(robot.place).id)
        computer(
                readProgram(File("day11.txt")),
                channel
        ) { (out, _) ->
            if (isPaint) {
                hull[robot.place] = Colour.get(out)
                isPaint = false
            } else {
                robot = robot.turn(Turn.get(out)).move()
                channel.send(Unit to hullColour(robot.place).id)
                isPaint = true
            }
        }
        val maxX = hull.keys.map { it.x }.max() ?: throw RuntimeException("no maxX")
        val maxY = hull.keys.map { it.y }.max() ?: throw RuntimeException("no maxY")
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                print(hullColour(Point(x, y)).char)
            }
            println()
        }
    }
}
