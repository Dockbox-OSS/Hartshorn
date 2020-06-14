package org.dockbox.darwin.core.text.actions

import TextAction
import org.dockbox.darwin.core.objects.targets.CommandSource
import org.dockbox.darwin.core.text.Text
import java.net.URL
import java.util.function.Consumer

abstract class ClickAction<R>

internal constructor(result: R) : TextAction<R>(result) {
    override fun applyTo(text: Text) {
        text.onClick(this)
    }

    class OpenUrl
    internal constructor(url: URL) : ClickAction<URL?>(url)

    class RunCommand
    internal constructor(command: String) : ClickAction<String?>(command)

    class ChangePage
    internal constructor(page: Int) : ClickAction<Int?>(page)

    class SuggestCommand
    internal constructor(command: String) : ClickAction<String?>(command)

    class ExecuteCallback
    internal constructor(result: Consumer<CommandSource?>) : ClickAction<Consumer<CommandSource?>?>(result)
}
