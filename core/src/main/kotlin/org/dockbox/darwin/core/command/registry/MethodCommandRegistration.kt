package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.i18n.Permission
import java.lang.reflect.Method

class MethodCommandRegistration(primaryAlias: String,
                                aliases: Array<String>,
                                command: Command,
                                val method: Method,
                                permissions: Array<Permission>?
) : AbstractCommandRegistration(primaryAlias, aliases, permissions, command, method)
