package kuyanov.vocabtest.server

import kotlinx.serialization.json.Json

val languages = listOf("en", "ru")
var wordsRegistry = mutableMapOf<String, List<String>>()
var levels = mapOf<String, Map<String, Int>>()

fun readWords() {
    languages.forEach { language ->
        wordsRegistry[language] = object {}.javaClass.classLoader
            .getResourceAsStream("words/${language}.txt")!!
            .bufferedReader().readLines()
    }
}

fun getWords(language: String): List<String> {
    return wordsRegistry[language]!!
}

fun readLevels() {
    levels = Json.decodeFromString(object {}.javaClass.classLoader
        .getResourceAsStream("levels.json")!!
        .bufferedReader().readText())
}

fun getLevels(language: String): Map<String, Int> {
    return levels[language]!!
}
