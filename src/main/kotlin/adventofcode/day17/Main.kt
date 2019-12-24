package adventofcode.day17

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    day17star1()
    day17star2()
}

fun isInterSection(x: Int, y: Int, map: MutableList<MutableList<Char>>) = x > 0 && y > 0 &&
        map[y][x] == '#' &&
        map[y - 1][x] == '#' &&
        map[y + 1][x] == '#' &&
        map[y][x - 1] == '#' &&
        map[y][x + 1] == '#'


fun day17star1() {
    val memory = readProgram(File("day17.txt"))
    val surfaceMap = mutableListOf<MutableList<Char>>(mutableListOf())
    runBlocking {
        computer(memory, Channel(1)) { ascii ->
            when (ascii) {
                10L -> surfaceMap.add(mutableListOf())
                else -> surfaceMap.last().add(ascii.toChar())
            }
        }
    }
    val sum = surfaceMap.mapIndexed { y, list ->
        list.mapIndexedNotNull { x, _ ->
            if (isInterSection(x, y, surfaceMap)) x * y else null
        }.sum()
    }.sum()
    println(sum)
}

fun day17star2() {
}
