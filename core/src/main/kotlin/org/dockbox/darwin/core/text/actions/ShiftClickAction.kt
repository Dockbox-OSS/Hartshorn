package org.dockbox.darwin.core.text.actions

import TextAction
import org.dockbox.darwin.core.objects.targets.CommandSource
import org.dockbox.darwin.core.text.Text
import java.net.URL
import java.util.function.Consumer

abstract class ShiftClickAction<R>

internal constructor(result: R) : TextAction<R>(result) {
    override fun applyTo(text: Text) {
        text.onShiftClick(this)
    }

    class OpenUrl
    internal constructor(url: URL) : ShiftClickAction<URL?>(url)

    class RunCommand
    internal constructor(command: String) : ShiftClickAction<String?>(command)

    class ChangePage
    internal constructor(page: Int) : ShiftClickAction<Int?>(page)

    class SuggestCommand
    internal constructor(command: String) : ShiftClickAction<String?>(command)

    class ExecuteCallback
    internal constructor(result: Consumer<CommandSource?>) : ShiftClickAction<Consumer<CommandSource?>?>(result)
}
