package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.i18n.I18NRegistry

class ClassCommandRegistration(primaryAlias: String,
                               aliases: Array<String>,
                               permission: I18NRegistry,
                               command: Command,
                               clazz: Class<*>,
                               val subcommands: Array<MethodCommandRegistration>
) : AbstractCommandRegistration(primaryAlias, aliases, permission, command, clazz)
