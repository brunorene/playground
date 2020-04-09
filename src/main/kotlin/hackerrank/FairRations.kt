package hackerrank

import java.util.*

fun fairRations(B: Array<Int>): Int {

}

fun main(args: Array<String>) {
    val scan = Scanner(System.`in`)

    val b = scan.nextLine().split(" ").map { it.trim().toInt() }.toTypedArray()

    val result = fairRations(b)

    println(result)
}