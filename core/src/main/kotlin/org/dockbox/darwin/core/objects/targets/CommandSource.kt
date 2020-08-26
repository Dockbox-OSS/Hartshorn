package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.I18N
import org.dockbox.darwin.core.text.Text

interface CommandSource : MessageReceiver {

    fun execute(command: String)

    object None : CommandSource {
        override fun execute(command: String) {
            throw UnsupportedOperationException("Attempted to execute command without source")
        }

        override fun send(text: I18N) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun send(text: Text) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun send(text: CharSequence) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: I18N) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: Text) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }

        override fun sendWithPrefix(text: CharSequence) {
            throw UnsupportedOperationException("Attempted to send message without source")
        }
    }

}

