package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command

abstract class AbstractCommandRegistration(val primaryAlias: String, val aliases: Array<String>, val permissions: Array<String>?, val command: Command, var sourceInstance: Any?) {

}
