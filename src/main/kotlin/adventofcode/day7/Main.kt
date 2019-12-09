package adventofcode.day7

import adventofcode.day5.computer
import adventofcode.day5.readProgram
import com.marcinmoskala.math.permutations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

fun main() {
    day7star1()
    day7star2()
}

fun day7star1() {
    val originalCode = readProgram(File("day7.txt"))
    println((0..4).toList().permutations().map { order ->
        var result = -1
        val queue0 = LinkedBlockingQueue<Int>(2).apply { add(order[0]); add(0) }
        val queue1 = LinkedBlockingQueue<Int>(2).apply { add(order[1]) }
        val queue2 = LinkedBlockingQueue<Int>(2).apply { add(order[2]) }
        val queue3 = LinkedBlockingQueue<Int>(2).apply { add(order[3]) }
        val queue4 = LinkedBlockingQueue<Int>(2).apply { add(order[4]) }
        GlobalScope.async {
            computer(originalCode.toMutableList(), queue0) { out -> queue1.add(out) }
            computer(originalCode.toMutableList(), queue1) { out -> queue2.add(out) }
            computer(originalCode.toMutableList(), queue2) { out -> queue3.add(out) }
            computer(originalCode.toMutableList(), queue3) { out -> queue4.add(out) }
            computer(originalCode.toMutableList(), queue4) { out -> result = out }
            result
        }
    }.map { result -> runBlocking { result.await() } }.max())
}

fun day7star2() {
    val originalCode = readProgram(File("day7.txt"))
    println((5..9).toList().permutations().map { order ->
        var result = -1
        val queue0 = LinkedBlockingQueue<Int>(10000).apply { add(order[0]); add(0) }
        val queue1 = LinkedBlockingQueue<Int>(10000).apply { add(order[1]) }
        val queue2 = LinkedBlockingQueue<Int>(10000).apply { add(order[2]) }
        val queue3 = LinkedBlockingQueue<Int>(10000).apply { add(order[3]) }
        val queue4 = LinkedBlockingQueue<Int>(10000).apply { add(order[4]) }
        println("trying $order")
        thread(start = true) {
            computer(originalCode.toMutableList(), queue0) { out -> queue1.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableList(), queue1) { out -> queue2.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableList(), queue2) { out -> queue3.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableList(), queue3) { out -> queue4.add(out) }
        }
        GlobalScope.async {
            computer(originalCode.toMutableList(), queue4) { out -> queue0.add(out); result = out }
            println("found result $result for $order")
            result
        }
    }.map { result -> runBlocking { result.await() } }.max())
}
