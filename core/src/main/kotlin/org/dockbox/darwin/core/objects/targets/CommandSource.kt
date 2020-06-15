package org.dockbox.darwin.core.objects.targets

interface CommandSource : MessageReceiver {

    fun execute(command: String)

}
