import kotlinx.css.*
import styled.StyleSheet

object CommonStyles : StyleSheet("CommonStyles", isStatic = true) {
    val container by css {
        display = Display.flex
        flexBasis = FlexBasis("800px")
        flexDirection = FlexDirection.column
        margin = Margin(0.px)
    }

    val header by css {
        padding = Padding(10.px)
    }

    val title by css {
        fontWeight = FontWeight.normal
        textAlign = TextAlign.center
    }

    val explanation by css {
        margin = Margin(0.px)
    }

    val words by css {
        display = Display.grid
        flexGrow = 1.0
        gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr, 1.fr, 1.fr)
        padding = Padding(10.px)
        media("(max-width: 800px)") {
            gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr, 1.fr)
        }
        media("(max-width: 600px)") {
            gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr)
        }
    }

    val status by css {
        margin = Margin(LinearDimension.auto)
    }

    val footer by css {
        alignItems = Align.center
        display = Display.flex
        flexDirection = FlexDirection.row
        justifyContent = JustifyContent.center
        padding = Padding(10.px)
        media("(min-width: 800px)") {
            padding = Padding(20.px)
        }
    }

    val footerButton by css {
        margin = Margin(0.px, 5.px)
    }

    val stage by css {
        marginRight = 10.px
    }
}
