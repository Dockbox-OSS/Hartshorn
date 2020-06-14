package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.text.Text

interface MessageReceiver : Target {
    fun send(text: Text)
    fun send(text: CharSequence)
    fun sendWithPrefix(text: Text)
    fun sendWithPrefix(text: CharSequence)
}
