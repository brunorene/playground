package hackerrank

import java.io.StringReader

fun main(args: Array<String>) {
    val reader = StringReader("8 2\n0 0 1 0 0 1 1 0\n").buffered(1)
    val (count, jump) = reader.readLine()!!
            .split(" ")
            .map { it.toInt() }
    val clouds = reader.readLine()!!
            .split(" ")
            .map { it.toInt() }
    var currentCloud = 0
    var score = 100
    do {
        currentCloud = (currentCloud + jump) % count
        score -= 1 + 2 * clouds[currentCloud]
    } while (currentCloud != 0)
    println(score)
}