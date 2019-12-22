package adventofcode.day16

import java.io.File
import kotlin.math.abs
import kotlin.math.ceil

fun main() {
    day16star1()
    day16star2()
}

fun readFrequency() = File("day16.txt")
        .readText()
        .map { it.toString().toLong() }

fun basePattern(index: Int, length: Int): List<Long> {
    val pattern = (0..index).map { 0L } +
            (0..index).map { 1L } +
            (0..index).map { 0L } +
            (0..index).map { -1L }
    val repeat = ceil(length.toDouble() / pattern.size.toDouble()).toLong() + 1L
    return (1..repeat).flatMap { pattern }.drop(1).take(length)
}

fun day16star1() {
    var freq = readFrequency()
    (0 until 100).map {
        freq = freq.indices.map { index ->
            val patt = basePattern(index, freq.size)
            abs(freq.zip(patt).map { (f, p) -> f * p }.sum() % 10)
        }
    }
    println(freq.take(8).joinToString(""))
}

fun day16star2() {
}
