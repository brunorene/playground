package adventofcode.day10

import java.awt.Point
import java.io.File
import kotlin.math.atan2

fun main() {
    day10star1()
    day10star2()
}

fun Point.degrees(center: Point): Double {
    var theta = atan2((y - center.y).toDouble(), (x - center.x).toDouble())
    theta += Math.PI / 2.0
    var angle = Math.toDegrees(theta)
    if (angle < 0) angle += 360
    return angle
}

data class Angle(
        val xDirection: Int,
        val yDirection: Int,
        val opposite: Int,
        val hypotenuse: Int
)

private fun Point.angle(end: Point): Angle {
    val opposite = (maxOf(x, end.x) - minOf(x, end.x)) * (maxOf(x, end.x) - minOf(x, end.x))
    val adjacent = (maxOf(y, end.y) - minOf(y, end.y)) * (maxOf(y, end.y) - minOf(y, end.y))
    val hypotenuse = opposite + adjacent
    val gcf = gcf(opposite, hypotenuse)
    return Angle(
            if (x - end.x > 0) 1 else if (x - end.x < 0) -1 else 0,
            if (y - end.y > 0) 1 else if (y - end.y < 0) -1 else 0,
            opposite / gcf,
            hypotenuse / gcf
    )
}

fun gcf(a: Int, b: Int): Int = if (b == 0) a else gcf(b, a % b)

private fun asteroids() = File("day10.txt").useLines { lines ->
    lines.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#')
                Point(x, y)
            else
                null
        }
    }.flatten().toList()
}

fun day10star1() {
    val asteroids = asteroids()
    println(asteroids.map { candidate ->
        asteroids
                .filter { it != candidate }
                .map { candidate.angle(it) }
                .distinct()
                .count()
    }.toSet().max())
}

private fun Point.squaredHypotenuse(end: Point): Int {
    val opposite = (maxOf(x, end.x) - minOf(x, end.x)) * (maxOf(x, end.x) - minOf(x, end.x))
    val adjacent = (maxOf(y, end.y) - minOf(y, end.y)) * (maxOf(y, end.y) - minOf(y, end.y))
    return opposite + adjacent
}

fun day10star2() {
    val asteroids = asteroids()
    val laserLocation = asteroids.maxBy { candidate ->
        asteroids
                .filter { it != candidate }
                .map { candidate.angle(it) }
                .distinct()
                .count()
    } ?: throw RuntimeException("empty asteroid list")
    val toDestroy = asteroids.filter { it != laserLocation }
            .groupBy { laserLocation.angle(it) }
            .map { (angle, points) -> angle to points.sortedBy { laserLocation.squaredHypotenuse(it) } }
            .toMap()
    val sortedKeys = toDestroy.keys.sortedBy { toDestroy[it]?.get(0)?.degrees(laserLocation) }

    println(toDestroy[sortedKeys[199]]?.let { it[0].x * 100 + it[0].y })
}
