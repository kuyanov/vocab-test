package kuyanov.vocabtest.server

import kotlin.math.*
import kotlin.random.Random

class Estimator(val language: String) {
    private val n = getWords(language).size
    private var iter = 0
    private var lb: Double = 3.0
    private var ub: Double = ln(n.toDouble())
    private val rnd = Random(System.currentTimeMillis())

    fun estimate(): Int {
        return round(exp((lb + ub) / 2)).toInt()
    }

    fun getQueries(): List<Int> {
        val numQueries = min(60, round(exp(ub)).toInt() - round(exp(lb)).toInt() + 1)
        val queries = mutableSetOf<Int>()
        while (queries.size < numQueries) {
            queries.add(round(exp(rnd.nextDouble(lb, ub))).toInt() - 1)
        }
        return queries.toList().sorted()
    }

    fun acceptAnswers(answers: List<Boolean>) {
        val cnt = answers.count { it }
        val mean = lb + (ub - lb) * cnt / answers.size
        var delta = (ub - lb) / sqrt(12.0 * answers.size)
        if (iter == 0) delta += 1
        var lb1 = mean - delta
        var ub1 = mean + delta
        lb1 = max(lb, min(ub - 0.1, lb1))
        ub1 = min(ub, max(lb + 0.1, ub1))
        lb = lb1
        ub = ub1
        ++iter
    }
}
