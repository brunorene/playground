package adventofcode.day17

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

data class Point(val x: Int, val y: Int)

fun main() {
    day17star1()
    day17star2()
}

fun List<List<Char>>.cell(point: Point) = this[point.y][point.x]

fun isInterSection(x: Int, y: Int, map: List<List<Char>>) = x > 0 && y > 0 &&
        map[y][x] == '#' &&
        map[y - 1][x] == '#' &&
        map[y + 1][x] == '#' &&
        map[y][x - 1] == '#' &&
        map[y][x + 1] == '#'


fun surfaceMap(): List<List<Char>> {
    val surfaceMap = mutableListOf<MutableList<Char>>(mutableListOf())
    val memory = readProgram(File("day17.txt"))
    runBlocking {
        computer(memory, Channel<Pair<Unit, Long>>(1)) { (ascii, _) ->
            when (ascii) {
                10L -> surfaceMap.add(mutableListOf())
                else -> surfaceMap.last().add(ascii.toChar())
            }
        }
    }
    return surfaceMap
}

fun printMap(map: List<List<Char>>) {
    map.forEach { line ->
        line.forEach { c -> print(c) }
        println()
    }
}

fun day17star1() {
    val surfaceMap = surfaceMap()
    val sum = surfaceMap.mapIndexed { y, list ->
        list.mapIndexedNotNull { x, _ ->
            if (isInterSection(x, y, surfaceMap)) x * y else null
        }.sum()
    }.sum()
    println(sum)
}

fun neighbours(place: Point, map: List<List<Char>>) {
    with(place) {
        listOf(
                copy(x = x - 1),
                copy(x = x + 1),
                copy(y = y - 1),
                copy(y = y + 1)
        )
    }.filter { p -> map.cell(p) == '#' }
}

fun day17star2() {
    val surfaceMap = surfaceMap()
//    val nextPlaces = LinkedList<Point>()
    
    printMap(surfaceMap)
}
