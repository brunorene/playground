package hackerrank

import java.util.*

fun ballsPerColor(container: Array<Array<Int>>) =
        container.reduce{ acc, next ->
            acc.zip(next).map { (a, b) -> a + b}.toTypedArray()
        }

// Complete the organizingContainers function below.
fun organizingContainers(container: Array<Array<Int>>): String {
    val perColor = ballsPerColor(container)
    container.forEach { c ->
        val count = c.sum()
        if(perColor.none { it == count })
            return "Impossible"
    }
    return "Possible"
}

fun main(args: Array<String>) {
    val scan = Scanner(System.`in`)

    val q = scan.nextLine().trim().toInt()

    for (qItr in 1..q) {
        val n = scan.nextLine().trim().toInt()

        val container = Array(n) { Array(n) { 0 } }

        for (i in 0 until n) {
            container[i] = scan.nextLine().split(" ").map { it.trim().toInt() }.toTypedArray()
        }

        val result = organizingContainers(container)

        println(result)
    }
}
