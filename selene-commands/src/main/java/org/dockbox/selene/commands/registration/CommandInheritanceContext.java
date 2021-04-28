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

package org.dockbox.selene.commands.registration;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;

public class CommandInheritanceContext extends AbstractRegistrationContext {

    private final List<MethodCommandContext> inheritedCommands = SeleneUtils.emptyConcurrentList();
    private final ResourceEntry missingArgumentResource;

    public CommandInheritanceContext(Command command, ResourceEntry missingArgumentResource) {
        super(command);
        this.missingArgumentResource = missingArgumentResource;
    }

    @Override
    public Exceptional<ResourceEntry> call(CommandSource source, CommandContext context) {
        Exceptional<MethodCommandContext> inheritedCommand = this.getParentExecutor();
        inheritedCommand.present(ctx -> ctx.call(source, context));
        return inheritedCommand.absent()
                ? Exceptional.of(this.missingArgumentResource)
                : Exceptional.none();
    }

    public Exceptional<MethodCommandContext> getParentExecutor() {
        return Exceptional.of(this.getInheritedCommands().stream()
                .filter(ctx -> ctx.getAliases().contains(""))
                .findFirst());
    }

    public List<MethodCommandContext> getInheritedCommands() {
        return this.inheritedCommands;
    }

    public void addInheritedCommand(MethodCommandContext context) {
        if (null != context) this.inheritedCommands.add(context);
    }
}
