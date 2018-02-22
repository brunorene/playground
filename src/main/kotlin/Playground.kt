import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

data class RecommendationData(val movie: Movie, val rating: Int, val timestamp: LocalDateTime)

data class Movie(val movieId: Int,
                 val name: String,
                 val releaseDate: LocalDate,
                 val url: URL?,
                 val genres: Set<String>) : Comparable<Movie> {

    override fun compareTo(other: Movie): Int = movieId.compareTo(other.movieId)

    override fun hashCode(): Int = movieId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (movieId != other.movieId) return false

        return true
    }
}

data class Viewer(val viewerId: Int,
                  val age: Int,
                  val genre: String,
                  val job: String,
                  val zipCode: String,
                  val recommendations: TreeSet<RecommendationData> = TreeSet { a, b -> a.movie.compareTo(b.movie) }) {

    override fun hashCode(): Int = viewerId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Viewer

        if (viewerId != other.viewerId) return false

        return true
    }

    fun similarTo(other: Viewer): Int = recommendations.flatMap { me ->
        other.recommendations
                .filter { me.movie == it.movie }
                .map { 4 - abs(me.rating - it.rating) }
                .toList()
    }.sum()

    override fun toString(): String {
        return "$viewerId\t${recommendations.map {
            "${it.movie.movieId.pad(4)}|${it.rating}"
        }.joinToString(",")}"
    }
}

fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())

fun String.toLocalDate(format: String): LocalDate = LocalDate.from(
        DateTimeFormatter.ofPattern(format).withLocale(Locale.US).parse(this))

fun Int.pad(size: Int): String =
        "${(0 until (maxOf(size, this.toString().length) - this.toString().length)).joinToString("") { "0" }}$this"

fun main(args: Array<String>) {
    val basePath = "/home/brsantos/Documentos/datasets/ml-100k"
    val genres = File("$basePath/u.genre")
            .bufferedReader(Charsets.UTF_8)
            .useLines {
                it.map { it.split(Regex("\\|")) }
                        .filter { it.size == 2 }
                        .sortedBy { it[1].toInt() }
                        .map { it[0] }
                        .toList()
            }

    val movies = File("$basePath/u.item")
            .bufferedReader(Charsets.UTF_8)
            .useLines {
                it.map { it.split(Regex("\\|")) }
                        .filter { it[5] == "0" }
                        .map {
                            Pair(it[0].toInt(),
                                 Movie(it[0].toInt(),
                                       it[1],
                                       it[2].toLocalDate("dd-MMM-yyyy"),
                                       if (it[4].isNotEmpty()) URL(it[4]) else null,
                                       setOf(*(5..23)
                                               .filter { idx -> it[idx] == "1" }
                                               .map { idx -> genres[idx - 5] }
                                               .toTypedArray())))
                        }
                        .toMap()
            }

    val viewers = File("$basePath/u.user")
            .bufferedReader(Charsets.UTF_8)
            .useLines {
                it.map { it.split(Regex("\\|")) }
                        .map {
                            Pair(it[0].toInt(),
                                 Viewer(it[0].toInt(),
                                        it[1].toInt(),
                                        it[2],
                                        it[3],
                                        it[4]))
                        }
                        .toMap()
            }

    File("$basePath/u.data")
            .bufferedReader(Charsets.UTF_8)
            .useLines {
                it.map { it.split(Regex("\\t")) }
                        .forEach {
                            val currentMovie = movies[it[1].toInt()]
                            if (currentMovie != null) {
                                viewers[it[0].toInt()]
                                        ?.recommendations
                                        ?.add(RecommendationData(currentMovie,
                                                                 it[2].toInt(),
                                                                 it[3].toLong().toLocalDateTime()))
                            }
                        }
            }
    runBlocking {
        produce(CommonPool) {
            viewers.values.forEach { me ->
                send(Pair(me, viewers.values.filter { me != it }.maxBy { me.similarTo(it) }))
            }
        }.consumeEach {
            println("""================================
${it.first}
${it.second}
${it.first.similarTo(it.second ?: it.first)}""")
        }
    }

}
