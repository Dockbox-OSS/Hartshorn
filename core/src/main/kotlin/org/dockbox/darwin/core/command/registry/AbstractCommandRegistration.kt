package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.i18n.Permission

abstract class AbstractCommandRegistration(val primaryAlias: String, val aliases: Array<String>, val permissions: Permission, val command: Command, var sourceInstance: Any?)
