package kuyanov.vocabtest.server

import Meta
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.*

fun HTML.index() {
    head {
        meta("viewport", "width=device-width, initial-scale=0.85, shrink-to-fit=no")
        title("Vocabulary test")
        link("https://fonts.gstatic.com", "preconnect")
        link("https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@300&display=swap", "stylesheet")
        link("https://use.fontawesome.com/releases/v5.11.2/css/all.css", "stylesheet")
        link("/static/keyframes.css", "stylesheet")
    }
    body {
        script(src = "/static/vocab-test.js") {}
    }
}

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    readWords()
    readLevels()

    val estimators = mutableMapOf<String, Estimator>()

    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }

        get("/getKey") {
            val language = call.parameters["language"]
            if (language == null || language !in languages) {
                call.respondText("Invalid language", status = HttpStatusCode.NotAcceptable)
                return@get
            }
            val key = UUID.randomUUID().toString()
            estimators[key] = Estimator(language)
            call.respondText(key)
        }

        get("/getMeta") {
            val language = call.parameters["language"]
            if (language == null || language !in languages) {
                call.respondText("Invalid language", status = HttpStatusCode.NotAcceptable)
                return@get
            }
            call.respond(Meta(getWords(language).size, getLevels(language)))
        }

        get("/getQueries") {
            val key = call.parameters["key"]
            val estimator = estimators[key]
            if (estimator == null) {
                call.respondText("Invalid key", status = HttpStatusCode.NotAcceptable)
                return@get
            }
            val queries = estimator.getQueries()
            val words = getWords(estimator.language)
            call.respond(queries.map { words[it] })
        }

        get("/estimate") {
            val key = call.parameters["key"]
            val estimator = estimators[key]
            if (estimator == null) {
                call.respondText("Invalid key", status = HttpStatusCode.NotAcceptable)
                return@get
            }
            val estimate = estimator.estimate()
            estimators.remove(key)
            call.respondText(estimate.toString())
        }

        post("/acceptAnswers") {
            val key = call.parameters["key"]
            val estimator = estimators[key]
            if (estimator == null) {
                call.respondText("Invalid key", status = HttpStatusCode.NotAcceptable)
                return@post
            }
            val answers: List<Boolean>
            try {
                answers = call.receive()
            } catch (e: Throwable) {
                call.respondText("Invalid data", status = HttpStatusCode.NotAcceptable)
                return@post
            }
            estimator.acceptAnswers(answers)
            call.respondText("Answers accepted")
        }

        static("/static") {
            resources()
        }
    }
}
