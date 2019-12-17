package adventofcode.day14

import java.io.File

fun main() {
    day14star1()
    day14star2()
}

fun readReactions() = File("day14.txt")
        .readLines()
        .map { line ->
            val pair = line.split(Regex(" => ")).let { it[0] to it[1].trim() }
            val input = pair.first.split(',')
                    .map { it.trim().split(' ') }
                    .map { it[0].toInt() to it[1] }
            val output = pair.second.split(' ').let { it[0].toInt() to it[1] }
            output to input
        }

fun day14star1() {
}

fun day14star2() {
}
