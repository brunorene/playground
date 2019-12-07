package adventofcode.day6

import java.io.File
import java.util.*

fun main() {
    day6star1()
    day6star2()
}

class Node(val name: String, var visited: Boolean = false, var distance: Int = 0) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "$name[visited=$visited, distance=$distance)]"
    }
}

private fun createGraph(): Map<Node, List<Node>> {
    val firstMap = File("day6.txt")
            .readLines()
            .map { l ->
                l.split(")")
                        .let { it[1] to it[0] }
            }.groupBy { Node(it.first) }
            .mapValues { (_, list) -> list.map { it.second } }
    return firstMap.mapValues { (_, list) ->
        list.map { value -> firstMap.keys.find { it.name == value } ?: Node(value) }
    }
}

private fun navigate(start: Node, graph: Map<Node, List<Node>>): Int = if (!graph.containsKey(start)) 0 else
    1 + (graph[start]?.map { navigate(it, graph) }?.sum() ?: 0)

fun day6star1() {
    val graph = createGraph()
    println(graph.keys.map { navigate(it, graph) }.sum())

}

private fun createBiDirectionalGraph(): Map<Node, Set<Node>> = mutableMapOf<Node, MutableSet<Node>>().also { map ->
    val orbits = File("day6.txt")
            .readLines()
            .map { it.split(")") }
            .map { it[1] to it[0] }
    val allNodes = orbits.flatMap { (a, b) -> listOf(a, b) }.associateWith { Node(it) }
    orbits.forEach { (center, satellite) ->
        map.computeIfAbsent(allNodes[center] ?: error("no node!")) { mutableSetOf() }
                .add(allNodes[satellite] ?: error("no node!"))
        map.computeIfAbsent(allNodes[satellite] ?: error("no node!")) { mutableSetOf() }
                .add(allNodes[center] ?: error("no node!"))
    }
}

fun day6star2() {
    val graph = createBiDirectionalGraph()
    val queue = LinkedList<Node>()
    graph.keys.find { it == Node("YOU") }?.visited = true
    graph.keys.find { it == Node("SAN") }?.visited = true
    val start = graph[Node("YOU")]?.first()
    if (start != null) {
        start.visited = true
        queue.add(start)
        while (queue.isNotEmpty()) {
            val current = queue.remove()
            graph[current]
                    ?.filter { !it.visited }
                    ?.forEach { node ->
                        node.visited = true
                        node.distance = current.distance + 1
                        queue.add(node)
                    }
        }
        println(graph[Node("SAN")]?.minBy { it.distance }?.distance)
    }
}
