package adventofcode.day12

import java.io.File
import kotlin.math.abs

fun main() {
    day12star1()
    day12star2()
}

data class Point(val x: Int, val y: Int, val z: Int) {
    fun gravity(other: Point) = Point(
            x + if (x == other.x) 0 else (x - other.x) / abs(x - other.x),
            y + if (y == other.y) 0 else (y - other.y) / abs(y - other.y),
            z + if (z == other.z) 0 else (z - other.z) / abs(z - other.z)
    )

    fun velocity(other: Point) = Point(x + other.x, y + other.y, z + other.z)
}

fun day12star1() {
    val regex = Regex("<x=([-0-9]+), y=([-0-9]+), z=([-0-9]+)>")
    val moonsPast = File("day12.txt")
            .readLines()
            .mapNotNull { line ->
                regex.matchEntire(line)?.let { res ->
                    Point(
                            res.groupValues[1].toInt(),
                            res.groupValues[2].toInt(),
                            res.groupValues[3].toInt()
                    ) to Point(0, 0, 0)
                }
            }
//    repeat(1) {
//        val moonsPresent = moonsPast.map { moon1 ->
//            moonsPast.fold(moon1.first) { acc, cur -> acc.gravity(cur.first) } to
//                    moonsPast.fold(moon1.second) { acc, cur -> acc.gravity(cur.second }
//        }
//        println(moonsPresent)
//    }
}

fun day12star2() {
}
