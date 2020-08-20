package org.dockbox.darwin.core.text.actions

import TextAction
import org.dockbox.darwin.core.text.Text

abstract class ShiftClickAction<R>

internal constructor(result: R) : TextAction<R>(result) {
    override fun applyTo(text: Text) {
        text.onShiftClick(this)
    }

    class InsertText
    internal constructor(text: Text) : ShiftClickAction<Text?>(text)

}
