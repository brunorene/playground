package hackerrank

import java.io.StringReader

fun main(args: Array<String>) {
    val reader = StringReader("50").buffered(1)
    val levels = reader.readLine()?.toInt() ?: 1
    println(recurse(levels, 5))
}

fun recurse(level: Int, accum: Int): Int = if (level == 1) accum / 2 else accum / 2 + recurse(level - 1, accum / 2 * 3)

