package adventofcode.day8

import java.io.File

fun main() {
    day8star1()
    day8star2()
}

const val COLS = 25
const val ROWS = 6

private fun List<String>.countChars(char: Char) = map { row -> row.count { c -> c == char } }.sum()

fun day8star1() {
    val layers = File("day8.txt")
            .readText()
            .replace("\n", "")
            .chunked(COLS)
            .chunked(ROWS)
    println(layers.minBy { it.map { row -> row.count { c -> c == '0' } }.sum() }?.run {
        countChars('1') * countChars('2')
    })
}

const val BLACK = '0'
const val WHITE = '1'
const val TRANSPARENT = '2'

private fun List<String>.readPixel(x: Int, y: Int) = this[y][x]

fun day8star2() {
    val layers = File("day8.txt")
            .readText()
            .replace("\n", "")
            .chunked(COLS)
            .chunked(ROWS)
    (0 until 6).forEach { y ->
        (0 until 25).forEach { x ->
            val pixel = layers.first { it.readPixel(x, y) != TRANSPARENT }.readPixel(x, y)
            print(when (pixel) {
                BLACK -> ' '
                WHITE -> '#'
                else -> 'x'
            })
        }
        println()
    }
}
