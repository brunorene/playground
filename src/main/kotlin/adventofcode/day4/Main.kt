package adventofcode.day4

fun main() {
    day4star1()
    day4star2()
}

private fun Int.digits() = toString().asSequence().map { (it - 48).toInt() }

fun day4star1() {
    println((284639..748759).filter { password ->
        val seq = password.digits().zipWithNext()
        seq.all { (a, b) -> a <= b } && seq.any { (a, b) -> a == b }
    }.count())
}

fun day4star2() {
    println((284639..748759).filter { password ->
        val list = listOf(-1) + password.digits().toList() + listOf(-1)
        password.digits().zipWithNext().all { (a, b) -> a <= b } &&
                (1 until list.size - 1).any { idx ->
                    list[idx - 1] == list[idx] &&
                            list[idx - 2] != list[idx - 1] &&
                            list[idx] != list[idx + 1]
                }
    }.count())
}
