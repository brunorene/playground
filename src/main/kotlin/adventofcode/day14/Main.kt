package adventofcode.day14

import java.io.File

fun main() {
    day14star1()
    day14star2()
}

fun readReactions() = File("day14-test.txt")
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
    val ore = "ORE"
    val reactions = readReactions()
    val expansion = mutableListOf<String>()
    val inputs = reactions.first { it.first.second == "FUEL" }
    inputs.second.forEach { (count, name) -> expansion.addAll((1..count).map { name }) }
    var oreCount = 0
    while (expansion.any { it != ore }) {
        // get first non-ORE element
        val name = expansion.first { it != ore }
        val reaction = reactions.first { it.first.second == name }
        repeat(reaction.first.first) { expansion.remove(name) }
        reaction.second.forEach { (count, name) -> expansion.addAll((1..count).map { name }) }
        oreCount += expansion.count { it == ore }
        expansion.removeIf { it == ore }
    }
    println("$oreCount")
}

fun day14star2() {
}
