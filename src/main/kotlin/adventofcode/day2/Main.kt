package adventofcode.day2

import java.io.File

fun main() {
    day2star1()
    day2star2()
}

fun computer(memory: MutableList<Int>): MutableList<Int> {
    (0 until memory.size step 4).forEach { opIdx ->
        when (memory[opIdx]) {
            1 -> memory[memory[opIdx + 3]] = memory[memory[opIdx + 1]] + memory[memory[opIdx + 2]]
            2 -> memory[memory[opIdx + 3]] = memory[memory[opIdx + 1]] * memory[memory[opIdx + 2]]
            99 -> return memory
        }
    }
    return memory
}

fun day2star1() {
    val memory = readProgram()
    memory[1] = 12
    memory[2] = 2
    val result = computer(memory)
    println(result[0])
}

private fun readProgram(): MutableList<Int> {
    return File("day2.txt")
            .readText()
            .replace("\n", "")
            .split(",")
            .map { it.toInt() }
            .toMutableList()
}

fun day2star2() {
    val memory = readProgram()
    val (noun, verb) = (0..99)
            .flatMap { n ->
                (0..99).map { v -> n to v }
            }.first { (noun, verb) ->
                val current = memory.toMutableList()
                current[1] = noun
                current[2] = verb
                val result = computer(current)
                result[0] == 19690720
            }
    println(100 * noun + verb)
}