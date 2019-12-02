package adventofcode.day1

import java.io.File

fun main(args: Array<String>) {
    day1star1()
    day1star2()
}

fun day1star1() {
    val file = File("day1.txt")
    println(file.useLines { lines -> lines.map { mass -> mass.toInt() / 3 - 2 }.sum() })
}

fun Int.fuel() = run {
    val value = this / 3 - 2
    if (value < 0) 0 else value
}

fun String.fuel() = this.toInt().fuel()

tailrec fun accumFuel(mass: Int, accumFuel: Int): Int =
        if (mass <= 0) accumFuel else accumFuel(mass.fuel(), accumFuel + mass.fuel())

fun day1star2() {
    val file = File("day1.txt")
    println(file.useLines { lines -> lines.map { accumFuel(it.toInt(), 0) }.sum() })
}

