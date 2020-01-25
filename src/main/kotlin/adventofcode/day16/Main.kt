package adventofcode.day16

import java.io.File
import kotlin.math.abs
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

private fun fft(input: List<Long>): List<Long> {
    val freq = input.toMutableList()
    for (repeat in 1..100) {
        println("repeat $repeat")
        for (index in 0 until freq.size) {
            val negativeStart = 2 + 3 * index
            val divider = 4 + 4 * index
            var sum = 0L
            for (fIdx in index until freq.size) {
                sum += when {
                    ((fIdx - index) / (index + 1)) % 4 == 0 -> freq[fIdx]
                    fIdx >= negativeStart && fIdx % divider in (negativeStart..negativeStart + index) -> -freq[fIdx]
                    else -> 0
                }
            }
            freq[index] = abs(sum % 10)
        }
    }
    return freq
}

private fun fft2(tail: List<Long>): List<Long> {
    val freq = tail.toMutableList()
    for (repeat in 1..100) {
        var lastSum = 0L
        println("repeat $repeat")
        for (index in (freq.size - 1) downTo 0) {
            lastSum += freq[index]
            freq[index] = lastSum % 10
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
    val offset = freq.take(7).joinToString("").toInt()
    val result = fft2((1..10000).flatMap { freq }.drop(offset))
    println(result.take(8).joinToString(""))
}
