package hackerrank

fun beautifulTriplets(d: Int, arr: Array<Int>): Int {
    var count = 0
    for (idx1 in arr.indices) {
        val slice1 = arr.withIndex().toList().slice(idx1 + 1 until arr.size)
                .firstOrNull { it.value - arr[idx1] == d }
        if (slice1 != null) {
            val slice2 = arr.slice(slice1.index + 1 until arr.size)
                    .firstOrNull { it - slice1.value == d }
            if (slice2 != null)
                count++
        }
    }
    return count
}

fun main(args: Array<String>) {

    val result = beautifulTriplets(3, arrayOf(1, 2, 4, 5, 7, 8, 10))

    println(result)
}