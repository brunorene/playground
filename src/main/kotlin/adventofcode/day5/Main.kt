package adventofcode.day5

import java.io.File

fun main() {
//    day5star1()
    day5star2()
}

data class OpCode(val code: Int, val modes: Triple<Mode, Mode, Mode>)

fun opcode(code: Int): OpCode {
    val completeCode = code.toString().padStart(5, '0')
    return OpCode(
            completeCode.substring(3).toInt(),
            Triple(
                    if (completeCode[2] == '0') Position(1) else Immediate(1),
                    if (completeCode[1] == '0') Position(2) else Immediate(2),
                    if (completeCode[0] == '0') Position(3) else Immediate(3)
            )
    )
}

sealed class Mode {
    abstract fun value(memory: List<Int>, opIdx: Int): Int
}

class Position(private val index: Int) : Mode() {
    override fun value(memory: List<Int>, opIdx: Int) = memory[memory[opIdx + index]]
}

class Immediate(private val index: Int) : Mode() {
    override fun value(memory: List<Int>, opIdx: Int) = memory[opIdx + index]
}

fun computer(memory: MutableList<Int>, input: Int): MutableList<Int> {
    var opIdx = 0
    while (opcode(memory[opIdx]).code != 99) {
        val opCode = opcode(memory[opIdx])
        val mode1 = opCode.modes.first::value
        val mode2 = opCode.modes.second::value
        when (opCode.code) {
            1 -> { // add
                memory[memory[opIdx + 3]] = mode1(memory, opIdx) + mode2(memory, opIdx)
                opIdx += 4
            }
            2 -> { // multiply
                memory[memory[opIdx + 3]] = mode1(memory, opIdx) * mode2(memory, opIdx)
                opIdx += 4
            }
            3 -> { // input
                memory[memory[opIdx + 1]] = input
                opIdx += 2
            }
            4 -> { // output
                println(memory[memory[opIdx + 1]])
                opIdx += 2
            }
            5 -> { // jump-if-true
                opIdx = if (mode1(memory, opIdx) != 0) mode2(memory, opIdx) else opIdx + 3
            }
            6 -> { // jump-if-false
                opIdx = if (mode1(memory, opIdx) == 0) mode2(memory, opIdx) else opIdx + 3
            }
            7 -> { // less-than
                memory[memory[opIdx + 3]] = if (mode1(memory, opIdx) < mode2(memory, opIdx)) 1 else 0
                opIdx += 4
            }
            8 -> { // equals
                memory[memory[opIdx + 3]] = if (mode1(memory, opIdx) == mode2(memory, opIdx)) 1 else 0
                opIdx += 4
            }
        }
    }
    return memory
}

fun day5star1() {
    computer(readProgram(), 1)
}

private fun readProgram(): MutableList<Int> {
    return File("day5.txt")
            .readText()
            .replace("\n", "")
            .split(",")
            .map { it.toInt() }
            .toMutableList()
}

fun day5star2() {
    computer(readProgram(), 5)
}