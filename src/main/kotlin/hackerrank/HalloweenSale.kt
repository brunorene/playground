package hackerrank

fun howManyGames(p: Int, d: Int, m: Int, s: Int): Int {
    var remainder = s
    var count = 0
    var currentPrice = p
    if (p <= s)
        while(true) {
            if (currentPrice < m)
                currentPrice = m
            remainder -= currentPrice
            currentPrice -= d
            if(remainder < 0)
                return count
            else
                count++
        }
    return count
}


fun main() {
    println(howManyGames(20, 3, 6, 80))
}