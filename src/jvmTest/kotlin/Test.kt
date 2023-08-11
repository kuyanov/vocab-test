import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kuyanov.vocabtest.server.*
import kotlin.math.*
import kotlin.random.Random
import kotlin.test.*

class ServerTests {
    private suspend fun runSimulations(client: HttpClient, cnt: Int, oracle: (String) -> Boolean)
            : List<Int> {
        return List(cnt) {
            val key = client.get("/getKey?language=en").body<String>()
            for (iter in 0..1) {
                val response = client.get("/getQueries?key=$key")
                val queries = response.body<List<String>>()
                val answers = queries.map(oracle)
                client.post("/acceptAnswers?key=$key") {
                    contentType(ContentType.Application.Json)
                    setBody(answers)
                }
            }
            val response = client.get("/estimate?key=$key")
            response.body()
        }
    }

    private fun calcMeanStd(values: List<Int>): Pair<Double, Double> {
        var mean = 0.0
        for (value in values) {
            mean += value.toDouble() / values.size
        }
        var std2 = 0.0
        for (value in values) {
            std2 += (value - mean) * (value - mean) / values.size
        }
        return Pair(mean, sqrt(std2))
    }

    private fun testPrefixVocabulary(size: Int, delta: Int, cnt: Int = 100) = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        readWords()
        val (mean, std) = calcMeanStd(runSimulations(client, cnt) {
            getWords("en").indexOf(it) < size
        })
        println("size = $size: $mean +- $std")
        assertTrue(mean >= size - delta && mean <= size + delta)
        assertTrue(std <= delta)
    }

    @Test
    fun prefixSize100() {
        testPrefixVocabulary(100, 20)
    }

    @Test
    fun prefixSize300() {
        testPrefixVocabulary(300, 60)
    }

    @Test
    fun prefixSize1000() {
        testPrefixVocabulary(1000, 200)
    }

    @Test
    fun prefixSize3000() {
        testPrefixVocabulary(3000, 600)
    }

    @Test
    fun prefixSize10000() {
        testPrefixVocabulary(10000, 2000)
    }

    @Test
    fun prefixSize30000() {
        testPrefixVocabulary(30000, 6000)
    }

    private fun testLinearVocabulary(size: Int, delta: Int, cnt: Int = 100) = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        readWords()
        val lb = ln(size.toDouble()) - 1.5
        val ub = ln(size.toDouble()) + 1.5
        val rnd = Random(System.currentTimeMillis())
        val (mean, std) = calcMeanStd(runSimulations(client, cnt) {
            val i = getWords("en").indexOf(it)
            rnd.nextDouble(lb, ub) > ln(i.toDouble())
        })
        println("size = ~$size: $mean +- $std")
        assertTrue(mean >= size - delta && mean <= size + delta)
        assertTrue(std <= delta)
    }

    @Test
    fun linearSize100() {
        testLinearVocabulary(100, 30)
    }

    @Test
    fun linearSize300() {
        testLinearVocabulary(300, 90)
    }

    @Test
    fun linearSize1000() {
        testLinearVocabulary(1000, 300)
    }

    @Test
    fun linearSize3000() {
        testLinearVocabulary(3000, 900)
    }

    @Test
    fun linearSize10000() {
        testLinearVocabulary(10000, 3000)
    }
}
