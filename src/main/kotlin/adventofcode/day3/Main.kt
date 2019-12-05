package adventofcode.day3

import java.io.File
import kotlin.math.absoluteValue

fun main() {
    day3star1()
    day3star2()
}

fun direction(id: Char) = when (id) {
    'R' -> Right
    'L' -> Left
    'U' -> Up
    'D' -> Down
    else -> throw RuntimeException("this direction does not exist")
}

fun <T, R> List<T>.scan(init: R, op: (R, T) -> R): List<R> {
    var calculated = init
    return map {
        calculated = op(calculated, it)
        calculated
    }
}

sealed class Direction {
    abstract fun toPair(before: Pair<Int, Int>): Pair<Int, Int>
}

object Up : Direction() {
    override fun toPair(before: Pair<Int, Int>) = with(before) { first to second - 1 }
}

object Down : Direction() {
    override fun toPair(before: Pair<Int, Int>) = with(before) { first to second + 1 }
}

object Right : Direction() {
    override fun toPair(before: Pair<Int, Int>) = with(before) { first + 1 to second }
}

object Left : Direction() {
    override fun toPair(before: Pair<Int, Int>) = with(before) { first - 1 to second }
}

private fun wires() = File("day3.txt").readLines().map { line ->
    line.split(",").flatMap { move ->
        (1..move.substring(1).toInt()).map { direction(move[0]) }
    }.scan(0 to 0) { pair, direction -> direction.toPair(pair) }
}.let { it[0] to it[1] }

fun day3star1() {
    println(candidates(wires()).map { it.first.absoluteValue + it.second.absoluteValue }.min())
}

fun day3star2() {
    val wires = wires()
    println(candidates(wires).map { wires.first.indexOf(it) + wires.second.indexOf(it) + 2 }.min())
}

private fun candidates(wires: Pair<List<Pair<Int, Int>>, List<Pair<Int, Int>>>): List<Pair<Int, Int>> {
    val comparator: Comparator<Pair<Int, Int>> = compareBy({ it.first }, { it.second })
    val firstWire = wires.first.distinct().sortedWith(comparator)
    return wires.second.filter { firstWire.binarySearch(it, comparator) >= 0 }
}