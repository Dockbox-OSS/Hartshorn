package org.dockbox.darwin.core.text.actions

import TextAction
import org.dockbox.darwin.core.text.Text

abstract class HoverAction<R> internal constructor(result: R) : TextAction<R>(result) {
    override fun applyTo(text: Text) {
        text.onHover(this)
    }

    class ShowText internal constructor(text: Text) : HoverAction<Text?>(text)

    // TODO : ShowItem, ShowEntity
}
