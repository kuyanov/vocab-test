import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.html.*
import kotlinx.html.js.*
import react.*
import react.dom.*
import styled.*
import web.dom.*
import web.location.location
import web.navigator.navigator
import web.timers.setTimeout
import kotlin.math.*

external interface ResultPageProps : Props {
    var language: String
    var numWords: Int
}

class ResultPage : RComponent<ResultPageProps, State>() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    private var meta: Meta? = null

    private suspend fun getMeta(): Meta {
        return client.get("/getMeta?language=${props.language}").body()
    }

    private fun progressFunc(numWords: Int): Double {
        return sqrt(numWords.toDouble() / meta!!.total)
    }

    private fun invProgressFunc(t: Double): Int {
        return round(t * t * meta!!.total).toInt()
    }

    private fun Element.appendSVGChild(
        tagName: String, attributes: Map<String, String>,
        text: String? = null
    ): Element {
        val child = document.createElementNS("http://www.w3.org/2000/svg", tagName)
        attributes.forEach { (key, value) ->
            child.setAttribute(key, value)
        }
        if (text != null) {
            child.appendChild(document.createTextNode(text))
        }
        this.appendChild(child)
        return child
    }

    private fun showLevel() {
        val entry = meta!!.levels.entries.sortedBy { it.value }
            .lastOrNull { it.value <= props.numWords }
        if (entry != null) {
            val (level, value) = entry
            val ratio = progressFunc(value)
            val status = document.getElementById("status")!!
            status.appendSVGChild(
                "circle", mapOf(
                    "r" to "21",
                    "cx" to (210 + 185 * sin(2 * PI * ratio)).toString(),
                    "cy" to (210 - 185 * cos(2 * PI * ratio)).toString()
                )
            ).setAttribute(
                "style", "animation: coloring 1ms linear forwards $ratio"
            )
            status.appendSVGChild(
                "text", mapOf(
                    "x" to (210 + 185 * sin(2 * PI * ratio)).toString(),
                    "y" to (210 - 185 * cos(2 * PI * ratio)).toString(),
                    "fill" to "white",
                    "dominant-baseline" to "middle",
                    "text-anchor" to "middle",
                    "font-size" to "1.2em"
                ), level
            )
        }
    }

    private fun animateStatus() {
        val status = document.getElementById("status")!!
        status.appendSVGChild(
            "circle", mapOf(
                "r" to "150",
                "cx" to "210",
                "cy" to "210",
                "fill" to "white",
                "stroke" to "whitesmoke",
                "stroke-width" to "5"
            )
        )
        val progressbar = status.appendSVGChild(
            "circle", mapOf(
                "r" to "150",
                "cx" to "210",
                "cy" to "210",
                "fill" to "transparent",
                "stroke-width" to "5",
                "transform" to "rotate(270)",
                "transform-origin" to "50%"
            )
        )
        val counter = status.appendSVGChild(
            "text", mapOf(
                "x" to "210",
                "y" to "210",
                "dominant-baseline" to "middle",
                "text-anchor" to "middle",
                "font-size" to "4em"
            )
        )
        for ((level, value) in meta!!.levels) {
            val ratio = progressFunc(value)
            status.appendSVGChild(
                "polygon", mapOf(
                    "points" to "210,52 215,45 205,45",
                    "transform" to "rotate(${360 * ratio})",
                    "transform-origin" to "50%"
                )
            ).setAttribute(
                "style", "animation: coloring 1ms linear forwards $ratio"
            )
            status.appendSVGChild(
                "text", mapOf(
                    "x" to (210 + 185 * sin(2 * PI * ratio)).toString(),
                    "y" to (210 - 185 * cos(2 * PI * ratio)).toString(),
                    "dominant-baseline" to "middle",
                    "text-anchor" to "middle",
                    "font-size" to "1.2em"
                ), level
            ).setAttribute(
                "style", "animation: coloring 1ms linear forwards $ratio"
            )
        }
        val durationMs = 2000
        val maxT = progressFunc(props.numWords)
        progressbar.setAttribute(
            "style",
            "animation: rotate ${durationMs}ms linear forwards $maxT"
        )
        counter.setAttribute(
            "style",
            "animation: coloring ${durationMs}ms linear forwards $maxT"
        )
        val cntIter = 100
        for (i in 1..cntIter) {
            setTimeout({
                counter.innerHTML = invProgressFunc(maxT * i / cntIter).toString()
            }, (durationMs * maxT * i / cntIter).toInt())
        }
        setTimeout({
            showLevel()
        }, (durationMs * maxT).toInt())
    }

    override fun componentDidMount() {
        MainScope().launch {
            meta = getMeta()
            animateStatus()
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css { +CommonStyles.container }
            styledDiv {
                css { +CommonStyles.header }
                styledH1 {
                    css { +CommonStyles.title }
                    +"Your vocabulary"
                }
            }
            styledSvg {
                attrs {
                    id = "status"
                }
                css { +CommonStyles.status }
                setProp("height", "420")
                setProp("width", "420")
            }
            styledDiv {
                css { +CommonStyles.footer }
                styledButton {
                    attrs {
                        id = "share"
                        onClickFunction = {
                            val shareText = """
                                My vocabulary size is ${props.numWords} words!
                                Test your vocab on ${location.href}
                            """.trimIndent()
                            navigator.clipboard.writeText(shareText).then {
                                document.getElementById("share")!!
                                    .setAttribute("disabled", "")
                            }
                        }
                    }
                    css {
                        +BaseStyles.myButton
                        +BaseStyles.blueButton
                        +CommonStyles.footerButton
                    }
                    span(classes = "fas fa-share-alt") {}
                    +" Share"
                }
                styledButton {
                    attrs {
                        onClickFunction = {
                            location.reload()
                        }
                    }
                    css {
                        +BaseStyles.myButton
                        +BaseStyles.greenButton
                        +CommonStyles.footerButton
                    }
                    span(classes = "fas fa-redo") {}
                    +" Check again"
                }
            }
        }
    }
}
