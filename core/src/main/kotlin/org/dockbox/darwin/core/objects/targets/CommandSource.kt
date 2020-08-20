package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.text.Text
import java.util.*

interface CommandSource : MessageReceiver {

    fun execute(command: String)

    object None : CommandSource {
        override fun execute(command: String) {
            throw UnsupportedOperationException("Attempted to execute command without source")
        }

        override fun send(text: Text) {
            TODO("Not yet implemented")
        }

        override fun send(text: CharSequence) {
            TODO("Not yet implemented")
        }

        override fun sendWithPrefix(text: Text) {
            TODO("Not yet implemented")
        }

        override fun sendWithPrefix(text: CharSequence) {
            TODO("Not yet implemented")
        }
    }

}

