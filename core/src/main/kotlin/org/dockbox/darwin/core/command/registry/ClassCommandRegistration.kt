package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.i18n.Permission

class ClassCommandRegistration(primaryAlias: String,
                               aliases: Array<String>,
                               permissions: Array<Permission>?,
                               command: Command,
                               clazz: Class<*>,
                               val subcommands: Array<MethodCommandRegistration>
) : AbstractCommandRegistration(primaryAlias, aliases, permissions, command, clazz) {


}
