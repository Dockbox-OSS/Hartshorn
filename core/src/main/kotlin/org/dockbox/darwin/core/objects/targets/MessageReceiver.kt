package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.I18N
import org.dockbox.darwin.core.text.Text

interface MessageReceiver : Target {
    fun send(text: I18N)
    fun send(text: Text)
    fun send(text: CharSequence)
    fun sendWithPrefix(text: I18N)
    fun sendWithPrefix(text: Text)
    fun sendWithPrefix(text: CharSequence)
}
