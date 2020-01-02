package adventofcode.day16

import java.io.File
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {
//    day16star1()
    day16star2()
}

fun readFrequency() = File("day16.txt")
        .readText()
        .map { it.toString().toLong() }

fun basePattern(index: Int, length: Int): List<Byte> {
    val list0 = mutableListOf<Byte>()
    val list1 = mutableListOf<Byte>()
    val listMinus1 = mutableListOf<Byte>()
    repeat(index + 1) {
        list0.add(0)
        list1.add(1)
        listMinus1.add(-1)
    }
    val repeat = ceil(length.toDouble() / ((index + 1) * 4)).toLong() + 1L
    return (1..repeat).flatMap { list0 + list1 + list0 + listMinus1 }
}

private fun fft(input: List<Long>): List<Long> {
    var freq = input
    val patterns = freq.indices.associateWith { basePattern(it, freq.size) }
    (0 until 100).forEach {
        println("phase $it")
        freq = freq.indices.map { index ->
            var sum = 0L
            for (fIdx in index until freq.size) {
                sum += patterns[index]!![fIdx + 1] * freq[fIdx]
            }
            abs(sum % 10)
        }
    }
    return freq
}

@ExperimentalTime
fun day16star1() {
    val howLong = measureTime {
        val result = fft(readFrequency())
        println(result.take(8).joinToString(""))
    }
    println("it took $howLong")
}

fun day16star2() {
    val freq = readFrequency()
    val result = fft((1..10000).flatMap { freq })
    val offset = result.take(7).joinToString("").toInt()
    println(result.drop(offset).take(8).joinToString(""))
}
