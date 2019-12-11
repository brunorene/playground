package adventofcode.day5

import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

fun main() {
    day5star1()
    day5star2()
}

data class OpCode(val code: Int, val modes: Triple<Mode, Mode, Mode>)

fun opcode(code: Long?, base: Long): OpCode {
    if (code == null)
        throw RuntimeException("no operation code!")
    val completeCode = code.toString().padStart(5, '0')

    val chooseMode = { c: Char, p: Long ->
        when (c) {
            '0' -> Position(p)
            '1' -> Immediate(p)
            '2' -> Relative(p, base)
            else -> throw RuntimeException("Unknown memory mode")
        }
    }

    return OpCode(
            completeCode.substring(3).toInt(),
            Triple(
                    chooseMode(completeCode[2], 1),
                    chooseMode(completeCode[1], 2),
                    chooseMode(completeCode[0], 3)
            )
    )
}

sealed class Mode {
    abstract fun value(memory: MutableMap<Long, Long>, opIdx: Long): Long
}

class Position(private val index: Long) : Mode() {
    override fun value(memory: MutableMap<Long, Long>, opIdx: Long) =
            memory[memory[opIdx + index]] ?: 0
}

class Immediate(private val index: Long) : Mode() {
    override fun value(memory: MutableMap<Long, Long>, opIdx: Long) =
            memory[opIdx + index] ?: 0
}

class Relative(private val index: Long, private val base: Long) : Mode() {
    override fun value(memory: MutableMap<Long, Long>, opIdx: Long) =
            memory[memory[opIdx + index]!! + base] ?: 0
}

fun computer(memory: MutableMap<Long, Long>, input: BlockingQueue<Long>, outputHandler: (Long) -> Unit = { }) {
    var opIdx = 0L
    var relativeBase = 0L
    while (opcode(memory[opIdx], relativeBase).code != 99) {
        val opCode = opcode(memory[opIdx], relativeBase)
        val mode1 = opCode.modes.first::value
        val mode2 = opCode.modes.second::value
        when (opCode.code) {
            1 -> { // add
                memory[memory[opIdx + 3]!!] = mode1(memory, opIdx) + mode2(memory, opIdx)
                opIdx += 4
            }
            2 -> { // multiply
                memory[memory[opIdx + 3]!!] = mode1(memory, opIdx) * mode2(memory, opIdx)
                opIdx += 4
            }
            3 -> { // input
                memory[memory[opIdx + 1]!!] = input.take()
                opIdx += 2
            }
            4 -> { // output
                outputHandler(mode1(memory, opIdx))
                opIdx += 2
            }
            5 -> { // jump-if-true
                opIdx = if (mode1(memory, opIdx) != 0L) mode2(memory, opIdx) else opIdx + 3
            }
            6 -> { // jump-if-false
                opIdx = if (mode1(memory, opIdx) == 0L) mode2(memory, opIdx) else opIdx + 3
            }
            7 -> { // less-than
                memory[memory[opIdx + 3]!!] = if (mode1(memory, opIdx) < mode2(memory, opIdx)) 1L else 0L
                opIdx += 4
            }
            8 -> { // equals
                memory[memory[opIdx + 3]!!] = if (mode1(memory, opIdx) == mode2(memory, opIdx)) 1L else 0L
                opIdx += 4
            }
            9 -> { // change relative base
                relativeBase += memory[opIdx + 1] ?: 0
                opIdx += 2
            }
        }
    }
}

fun day5star1() {
    computer(readProgram(File("day5.txt")), LinkedBlockingQueue<Long>(1).apply { add(1L) }) { println(it) }
}

fun readProgram(file: File) = file
        .readText()
        .replace("\n", "")
        .split(",")
        .mapIndexed { index, s -> index.toLong() to s.toLong() }
        .toMap(mutableMapOf())

fun day5star2() {
    computer(readProgram(File("day5.txt")), LinkedBlockingQueue<Long>(1).apply { add(5L) }) { println(it) }
}
