package org.dockbox.darwin.core.command

import org.dockbox.darwin.core.command.context.CommandContext
import org.dockbox.darwin.core.objects.targets.CommandSource

@FunctionalInterface
interface CommandRunnerFunction {
    fun run(src: CommandSource, ctx: CommandContext)
}
