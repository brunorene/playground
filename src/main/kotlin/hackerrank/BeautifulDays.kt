package hackerrank

import java.io.StringReader

fun main(args: Array<String>) {
    val reader = StringReader("20 23 6").buffered(1)
    val (a, b, d) = reader.readLine()?.split(" ")?.map { it.toInt() } ?: listOf(0, 0, 0)
    println((a..b).map { if ((it - it.reverse()) % d == 0) 1 else 0 }.sum())
}

fun Int.reverse(): Int = this.toString().reversed().toInt()