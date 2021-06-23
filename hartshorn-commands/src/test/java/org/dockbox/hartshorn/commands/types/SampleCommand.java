/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.commands.types;

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;

@Command("demo")
public class SampleCommand {

    @Command(arguments = "<remaining{remainingString}>")
    public void parent(CommandContext context) {
    }

    @Command(value = "sub", arguments = "<argument{Int}> --skip remainingInt")
    public void sub(CommandContext context) {
    }

    @Command(value = "sub sub", arguments = "<service{Service}>")
    public void subsub(CommandContext context) {
    }

    @Command(value = "complex", arguments = "<required{String}> [optional{String}]  [enum{org.dockbox.hartshorn.commands.types.CommandValueEnum}] --flag --vflag String -s")
    public void complex(CommandContext context) {
    }

    @Command(value = "group", arguments = "[group <requiredA> <requiredB>]")
    public void group(CommandContext context) {
    }

    @Command(value = "arguments", arguments = "<required{String}> [optional{String}] --flag String")
    public void arguments(CommandSource source, CommandContext context, String required, String optional, String flag) {
    }
}
