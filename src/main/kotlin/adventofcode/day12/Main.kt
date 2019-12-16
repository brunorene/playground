package adventofcode.day12

import java.io.File
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    day12star1()
    day12star2()
}

class Moon(
        val id: Int,
        private val position: Point,
        private val velocity: Point = Point(0, 0, 0)
) {
    val energy
        get() = position.sum() * velocity.sum()

    fun applyGravity(other: Moon) = Moon(
            id,
            position,
            Point(
                    velocity.x - position.x.compareTo(other.position.x).sign,
                    velocity.y - position.y.compareTo(other.position.y).sign,
                    velocity.z - position.z.compareTo(other.position.z).sign
            )
    )

    fun applyVelocity() = Moon(
            id,
            Point(position.x + velocity.x, position.y + velocity.y, position.z + velocity.z),
            velocity
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Moon

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "Moon(id=$id, position=$position, velocity=$velocity)"
    }
}

data class Point(val x: Int, val y: Int, val z: Int) {
    fun sum() = abs(x) + abs(y) + abs(z)

    override fun toString(): String {
        return "Point(x=${x.toString().padStart(3)}, y=${y.toString().padStart(3)}, z=${z.toString().padStart(3)})"
    }

}

fun readMoons(): List<Moon> {
    val regex = Regex("<x=([-0-9]+), y=([-0-9]+), z=([-0-9]+)>")
    return File("day12.txt")
            .readLines()
            .mapIndexedNotNull { index, line ->
                regex.matchEntire(line)?.let { res ->
                    Moon(
                            index,
                            Point(
                                    res.groupValues[1].toInt(),
                                    res.groupValues[2].toInt(),
                                    res.groupValues[3].toInt()
                            ))
                }
            }
}

fun day12star1() {
    var moons = readMoons()
    moons.forEach { m -> println(m) }
    (1..1000).forEach {
        val newVelocities = moons.map { moon ->
            moons.filter { m -> m != moon }
                    .fold(moon) { acc, other -> acc.applyGravity(other) }
        }
        moons = newVelocities.map { m -> m.applyVelocity() }
        if (it % 100 == 0) {
            println(it)
            moons.forEach { m -> println(m) }
        }
    }
    println(moons.map { it.energy }.sum())
}

fun day12star2() {
}
