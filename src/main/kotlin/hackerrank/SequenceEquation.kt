package hackerrank

import java.io.StringReader

fun main(args: Array<String>) {
    val reader = StringReader("3\n2 3 1\n").buffered(1)
    val count = reader.readLine()!!.toInt()
    val numbers = reader.readLine()!!
            .split(" ")
            .map { it.toInt() }
            .mapIndexed { idx, value -> Pair(value, idx + 1) }
            .toMap()
    (1..count).forEach { println(numbers[numbers[it]]) }
}