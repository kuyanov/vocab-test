import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

val globalStyles = CssBuilder().apply {
    "*, *::after, *::before" {
        boxSizing = BoxSizing.borderBox
    }

    html {
        display = Display.flex
        minHeight = 100.pct
    }

    body {
        display = Display.flex
        flex = Flex(1.0)
        flexDirection = FlexDirection.row
        fontFamily = "'Noto Sans JP', sans-serif"
        fontSize = 18.px
        justifyContent = JustifyContent.center
        margin = Margin(0.px)
    }
}

object BaseStyles : StyleSheet("BaseStyles", isStatic = true) {
    val myButton by css {
        border = Border.none
        borderRadius = 1.em
        color = Color.black.withAlpha(0.8)
        display = Display.inlineBlock
        fontFamily = "inherit"
        fontSize = LinearDimension.inherit
        lineHeight = LineHeight("1.5em")
        outline = Outline.none
        padding = Padding(0.5.em, 1.5.em)
        textDecoration = TextDecoration.none
        disabled {
            backgroundColor = Color.whiteSmoke
            color = Color.lightGrey
        }
    }

    val blueButton by css {
        backgroundColor = Color.lightBlue
        not("[disabled]") {
            hover {
                backgroundColor = rgb(149, 208, 228)
            }
            active {
                backgroundColor = rgb(125, 188, 209)
            }
        }
    }

    val greenButton by css {
        backgroundColor = Color.lightGreen
        not("[disabled]") {
            hover {
                backgroundColor = rgb(124, 228, 124)
            }
            active {
                backgroundColor = rgb(104, 214, 104)
            }
        }
    }

    val checkbox by css {
        alignItems = Align.center
        display = Display.flex
        flexDirection = FlexDirection.row
        "input" {
            display = Display.none
        }
        "input + label" {
            border = Border(1.px, BorderStyle.solid, rgb(187, 187, 187))
            borderRadius = 25.pct
            display = Display.flex
            flexShrink = 0
            height = 1.25.em
            margin = Margin(LinearDimension.auto, 5.px, LinearDimension.auto, 0.px)
            width = 1.25.em
        }
        "input:not([disabled]) + label" {
            backgroundColor = Color.white
        }
        "input:not([disabled]) + label:hover" {
            backgroundColor = Color.whiteSmoke
        }
        "input:not([disabled]) + label:active" {
            backgroundColor = Color("#e5e5e5")
        }
        "input:disabled + label" {
            backgroundColor = Color.whiteSmoke
            borderColor = Color.lightGrey
        }
        "input + label span" {
            color = Color.white
            fontSize = 0.66.em
            margin = Margin(LinearDimension.auto)
            visibility = Visibility.hidden
        }
        "input:checked:not([disabled]) + label" {
            backgroundColor = rgb(104, 214, 104)
            borderColor = Color.transparent
            boxShadow += BoxShadow(Color.lightGrey, 0.px, 0.px, 10.px)
            color = Color.white
        }
        "input:checked:not([disabled]) + label:hover" {
            backgroundColor = rgb(96, 194, 96)
        }
        "input:checked:not([disabled]) + label:active" {
            backgroundColor = rgb(87, 175, 87)
        }
        "input:checked:disabled + label" {
            backgroundColor = Color.lightGrey
            borderColor = Color.transparent
        }
        "input:checked + label span" {
            visibility = Visibility.visible
        }
        "input + label + label" {
            marginRight = 5.px
            userSelect = UserSelect.none
        }
    }

    val scrollable by css {
        height = 0.px
        overflow = Overflow.auto
        media("(max-width: 800px)") {
            borderBottom = Border(1.px, BorderStyle.solid, Color.whiteSmoke)
            borderTop = Border(1.px, BorderStyle.solid, Color.whiteSmoke)
        }
    }

    val customScroll by css {
        "::-webkit-scrollbar" {
            backgroundColor = Color.transparent
            width = 11.px
        }
        "::-webkit-scrollbar-thumb" {
            backgroundColor = Color.grey
            border = Border(2.px, BorderStyle.solid, Color.white)
            borderRadius = 6.px
        }
    }
}
