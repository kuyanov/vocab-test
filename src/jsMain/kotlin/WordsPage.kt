import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.html.js.*
import react.*
import react.dom.*
import styled.*
import web.dom.*

external interface WordsPageProps : Props {
    var language: String
    var showResultPageFunc: (Int) -> Unit
}

external interface WordsPageState : State {
    var stage: Int
    var queries: Array<String>
    var answers: Array<Boolean>
}

class WordsPage : RComponent<WordsPageProps, WordsPageState>() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    private var key: String? = null

    init {
        state.stage = 0
        state.queries = arrayOf()
        state.answers = arrayOf()
    }

    private suspend fun getKey(): String {
        return client.get("/getKey?language=${props.language}").body()
    }

    private suspend fun getQueries(): Array<String> {
        return client.get("/getQueries?key=$key").body()
    }

    private suspend fun getNumWords(): Int {
        return client.get("/estimate?key=$key").body()
    }

    private suspend fun sendAnswers() {
        client.post("/acceptAnswers?key=$key") {
            contentType(ContentType.Application.Json)
            setBody(state.answers)
        }
    }

    private suspend fun next() {
        if (state.stage == 2) {
            val numWords = getNumWords()
            props.showResultPageFunc(numWords)
        } else {
            setState {
                stage++
            }
            val downloadedQueries = getQueries()
            document.getElementById("words")!!.scroll(0.0, 0.0)
            setState {
                queries = downloadedQueries
                answers = Array(queries.size) { false }
            }
        }
    }

    override fun componentDidMount() {
        MainScope().launch {
            key = getKey()
            next()
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css { +CommonStyles.container }
            styledDiv {
                css { +CommonStyles.header }
                styledH1 {
                    css { +CommonStyles.title }
                    +"Test your vocabulary!"
                }
                styledP {
                    css { +CommonStyles.explanation }
                    +"""Check the words for which you know at least one definition.
                        If you're not sure, leave it blank.
                    """.trimIndent()
                }
            }
            styledDiv {
                attrs {
                    id = "words"
                }
                css {
                    +BaseStyles.scrollable
                    +BaseStyles.customScroll
                    +CommonStyles.words
                }
                for ((i, word) in state.queries.withIndex()) {
                    styledDiv {
                        css { +BaseStyles.checkbox }
                        input(type = InputType.checkBox) {
                            attrs {
                                checked = state.answers[i]
                                id = i.toString()
                                onClickFunction = {
                                    setState {
                                        answers[i] = !answers[i]
                                    }
                                }
                            }
                        }
                        label {
                            attrs {
                                htmlFor = i.toString()
                            }
                            span(classes = "fas fa-check") {}
                        }
                        label {
                            attrs {
                                htmlFor = i.toString()
                            }
                            +word
                        }
                    }
                }
            }
            styledDiv {
                css { +CommonStyles.footer }
                styledDiv {
                    css { +CommonStyles.stage }
                    +"Stage "
                    span {
                        +state.stage.toString()
                    }
                    +"/2"
                }
                styledButton {
                    attrs {
                        onClickFunction = {
                            MainScope().launch {
                                sendAnswers()
                                next()
                            }
                        }
                    }
                    css {
                        +BaseStyles.myButton
                        +BaseStyles.greenButton
                        +CommonStyles.footerButton
                    }
                    +"Next"
                    styledSpan {
                        css {
                            marginLeft = 0.25.em
                        }
                        attrs {
                            classes = setOf("fas", "fa-chevron-right")
                        }
                    }
                }
            }
        }
    }
}
