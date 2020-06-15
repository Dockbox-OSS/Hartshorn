package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command

class ClassCommandRegistration(primaryAlias: String,
                               aliases: Array<String>,
                               permissions: Array<String>?,
                               command: Command,
                               val clazz: Class<*>,
                               val subcommands: Array<MethodCommandRegistration>
) : AbstractCommandRegistration(primaryAlias, aliases, permissions, command) {


}
