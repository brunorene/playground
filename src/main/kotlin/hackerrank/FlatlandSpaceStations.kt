package hackerrank

import java.util.*
import kotlin.math.min


// Complete the flatlandSpaceStations function below.
fun flatlandSpaceStations(n: Int, c: Array<Int>): Int? {
    val sorted = c.sorted()
    return (0 until n)
            .mapNotNull { city ->
                val index = sorted.binarySearch(city)
                if (index >= 0) 0 else {
                    when (val insertIndex = -index - 1) {
                        0 -> sorted[0] - city
                        sorted.size -> city - sorted[sorted.size - 1]
                        else -> min(sorted[insertIndex] - city, city - sorted[insertIndex - 1])
                    }
                }
            }
            .max()
}

fun main(args: Array<String>) {
    val scan = Scanner("""
        20 5
        13 1 11 10 6
    """.trimIndent())

    val nm = scan.nextLine().split(" ")

    val n = nm[0].trim().toInt()

    val c = scan.nextLine().split(" ").map { it.trim().toInt() }.toTypedArray()

    val result = flatlandSpaceStations(n, c)

    println(result)
}