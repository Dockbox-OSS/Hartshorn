package org.dockbox.darwin.core.command.registry

import org.dockbox.darwin.core.annotations.Command
import org.dockbox.darwin.core.i18n.I18NRegistry
import org.dockbox.darwin.core.i18n.Permission
import java.lang.reflect.Method

class MethodCommandRegistration(primaryAlias: String,
                                aliases: Array<String>,
                                command: Command,
                                val method: Method,
                                permission: I18NRegistry
) : AbstractCommandRegistration(primaryAlias, aliases, permission, command, method)
