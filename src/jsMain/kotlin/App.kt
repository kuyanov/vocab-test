import react.*
import web.location.location
import web.url.URLSearchParams

enum class Page {
    WORDS_PAGE,
    RESULT_PAGE
}

external interface AppState : State {
    var page: Page
    var numWords: Int?
}

class App : RComponent<Props, AppState>() {
    private val language = URLSearchParams(location.search)["language"] ?: "en"

    init {
        state.page = Page.WORDS_PAGE
        state.numWords = null
    }

    override fun RBuilder.render() {
        when (state.page) {
            Page.WORDS_PAGE -> child(WordsPage::class) {
                attrs {
                    language = this@App.language
                    showResultPageFunc = {
                        setState {
                            page = Page.RESULT_PAGE
                            numWords = it
                        }
                    }
                }
            }

            Page.RESULT_PAGE -> child(ResultPage::class) {
                attrs {
                    language = this@App.language
                    numWords = state.numWords!!
                }
            }
        }
    }
}
