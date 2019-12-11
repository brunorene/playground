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
    println((0L..4L).toList().permutations().map { order ->
        var result = -1L
        val queue0 = LinkedBlockingQueue<Long>(2).apply { add(order[0]); add(0) }
        val queue1 = LinkedBlockingQueue<Long>(2).apply { add(order[1]) }
        val queue2 = LinkedBlockingQueue<Long>(2).apply { add(order[2]) }
        val queue3 = LinkedBlockingQueue<Long>(2).apply { add(order[3]) }
        val queue4 = LinkedBlockingQueue<Long>(2).apply { add(order[4]) }
        GlobalScope.async {
            computer(originalCode.toMutableMap(), queue0) { out -> queue1.add(out) }
            computer(originalCode.toMutableMap(), queue1) { out -> queue2.add(out) }
            computer(originalCode.toMutableMap(), queue2) { out -> queue3.add(out) }
            computer(originalCode.toMutableMap(), queue3) { out -> queue4.add(out) }
            computer(originalCode.toMutableMap(), queue4) { out -> result = out }
            result
        }
    }.map { result -> runBlocking { result.await() } }.max())
}

fun day7star2() {
    val originalCode = readProgram(File("day7.txt"))
    println((5L..9L).toList().permutations().map { order ->
        var result = -1L
        val queue0 = LinkedBlockingQueue<Long>(10000).apply { add(order[0]); add(0) }
        val queue1 = LinkedBlockingQueue<Long>(10000).apply { add(order[1]) }
        val queue2 = LinkedBlockingQueue<Long>(10000).apply { add(order[2]) }
        val queue3 = LinkedBlockingQueue<Long>(10000).apply { add(order[3]) }
        val queue4 = LinkedBlockingQueue<Long>(10000).apply { add(order[4]) }
        println("trying $order")
        thread(start = true) {
            computer(originalCode.toMutableMap(), queue0) { out -> queue1.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableMap(), queue1) { out -> queue2.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableMap(), queue2) { out -> queue3.add(out) }
        }
        thread(start = true) {
            computer(originalCode.toMutableMap(), queue3) { out -> queue4.add(out) }
        }
        GlobalScope.async {
            computer(originalCode.toMutableMap(), queue4) { out -> queue0.add(out); result = out }
            println("found result $result for $order")
            result
        }
    }.map { result -> runBlocking { result.await() } }.max())
}
