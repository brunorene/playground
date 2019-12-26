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
        val position: Point,
        val velocity: Point = Point(0, 0, 0)
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
    (1..1000).forEach { _ ->
        val newVelocities = moons.map { moon ->
            moons.filter { m -> m != moon }
                    .fold(moon) { acc, other -> acc.applyGravity(other) }
        }
        moons = newVelocities.map { m -> m.applyVelocity() }
    }
    println(moons.map { it.energy }.sum())
}

private fun mdc(num1: Long, num2: Long): Long {
    var a = num1
    var b = num2
    while (b != 0L) {
        val r = a % b
        a = b
        b = r
    }
    return a
}

private fun mmc(a: Long, b: Long) = a * (b / mdc(a, b))

fun day12star2() {
    println("-----//-----")
    var moons = readMoons()
    val stateX = { moon: Moon -> moon.position.x to moon.velocity.x }
    val stateY = { moon: Moon -> moon.position.y to moon.velocity.y }
    val stateZ = { moon: Moon -> moon.position.z to moon.velocity.z }
    val minimumState = listOf('x' to stateX, 'y' to stateY, 'z' to stateZ)
            .flatMap { (id, getState) ->
                moons.mapIndexed { index, pivot ->
                    id to (1..40).map {
                        val pivotState = getState(pivot)
                        var count = 0L
                        do {
                            val newVelocities = moons.map { moon ->
                                moons.filter { m -> m != moon }
                                        .fold(moon) { acc, other -> acc.applyGravity(other) }
                            }
                            moons = newVelocities.map { m -> m.applyVelocity() }
                            count++
                        } while (pivotState != getState(moons[index]))
                        count
                    }
                }
            }
    minimumState.forEach { println("${it.first} - ${it.second.joinToString("\t")}") }
    println(mmc(mmc(286332, 161428), 96236))
}
// x
// 286332 - 2110+1344+6335+48383+169988+48383+6335+1344+2110
// 286332 - 9136+34488+199084+34488+9136
// 286332 - 50924+184484+50924
// 286332 - 501+9998+501+80999+44446+349+11872+11872+349+44446+80999
// y
// 161428 - 161428
// 161428 - 140738+20690
// 161428 - 17326+19832+17370+66168+40732
// 161428 - 45112+15148+101168
// z
// 96236 - 7194+1313+1079+34390+7259+6679+13746+5876+3933+97+384+11556+2730
