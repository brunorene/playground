package hackerrank

import java.util.*
import kotlin.math.abs

fun minimumDistances(a: Array<Int>): Int {
    val distances = mutableMapOf<Int, TreeSet<Int>>()
    a.forEachIndexed { index, value ->
        distances.compute(value) { _, v -> v?.also { it.add(index) } ?: TreeSet(setOf(index)) }
    }
    return distances.values.mapNotNull { set ->
        set.zipWithNext().map { (a, b) -> abs(a - b) }.min()
    }.min() ?: -1
}

fun main() {
    val a = arrayOf(7, 1, 3, 4, 1, 7)
    println(minimumDistances(a))
}