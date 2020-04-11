package hackerrank

import java.io.StringReader
import java.util.*

fun String.impossibleToBeHappy() = filter { it != '_' }.groupBy { it }.any { (_, v) -> v.size == 1 } ||
        !contains("_")

fun String.unhappy(index: Int) = when (index) {
    0 -> length == 1 || get(index) != get(index + 1)
    length - 1 -> get(index) != get(index - 1)
    else -> get(index - 1) != get(index) && get(index) != get(index + 1)
}

fun String.allAreHappy() = filterIndexed { index, _ -> unhappy(index) }.isEmpty() ||
        groupBy { it }.all { (k, _) -> k == '_' }

// Complete the happyLadybugs function below.
fun happyLadybugs(b: String): String {
    if (b.allAreHappy()) return "YES"
    if (b.impossibleToBeHappy()) return "NO"
    return "YES"
}

fun main(args: Array<String>) {
    val scan = Scanner(StringReader(
            """5
5
AABBC
7
AABBC_C
1
_
10
DD__FQ_QQF
6
AABCBC"""
    ))

    val g = scan.nextLine().trim().toInt()

    for (gItr in 1..g) {
        val n = scan.nextLine().trim().toInt()

        val b = scan.nextLine()

        val result = happyLadybugs(b)

        println(result)
    }
}