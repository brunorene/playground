package hackerrank

import kotlin.math.abs

fun canShareRations(B: List<Int>) = B.filter { it % 2 == 1 }.count() % 2 == 0

fun nearestOdd(B: List<Int>, index: Int) = B
        .mapIndexed { idx, value -> idx to value }
        .filter { (idx, value) -> idx != index && value % 2 == 1 }
        .map { (idx, _) -> idx to abs(idx - index) }
        .minBy { (_, diff) -> diff }?.first

fun shareBread(B: List<Int>, index: Int): Pair<Int, List<Int>> {
    val nearest = nearestOdd(B, index) ?: return -1 to emptyList()
    val direction = if (nearest < index) -1 else 1
    val nextList = B.mapIndexed { idx, value -> if (idx == index || idx == index + direction) value + 1 else value }
    val addedBread = nextList.sum() - B.sum()
    return addedBread to nextList
}

fun isFinished(B: List<Int>) = B.all { it % 2 == 0 }

fun fairRations(B: Array<Int>): String {
    var inputList = B.toList()
    if (!canShareRations(inputList)) return "NO"
    var countBread = 0
    while (!isFinished(inputList)) {
        val (addedBread, newList) = shareBread(inputList, inputList
                .mapIndexed { index, count -> index to count }.first { (_, count) -> count % 2 == 1 }.first)
        inputList = newList
        countBread += addedBread
    }
    return countBread.toString()
}

fun main(args: Array<String>) {
    val b = "2 3 4 5 6".split(" ").map { it.trim().toInt() }.toTypedArray()

    val result = fairRations(b)

    println(result)
}