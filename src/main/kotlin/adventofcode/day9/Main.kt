package adventofcode.day9

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import java.io.File
import java.util.concurrent.LinkedBlockingQueue

fun main() {
    day9star1()
    day9star2()
}

fun day9star1() {
    computer(readProgram(File("day9.txt")), LinkedBlockingQueue<Long>(1).apply { add(1L) }) { println(it) }
}


fun day9star2() {

}