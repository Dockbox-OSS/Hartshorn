package org.dockbox.darwin.core.objects.targets

interface CommandSource : Target {

    fun execute(command: String)

}
