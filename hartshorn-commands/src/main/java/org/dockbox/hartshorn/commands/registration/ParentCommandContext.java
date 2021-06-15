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

package org.dockbox.hartshorn.commands.registration;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.reflect.Method;
import java.util.List;

public class ParentCommandContext extends MethodCommandContext {

    private final List<AbstractCommandContext> inheritedCommands = HartshornUtils.emptyConcurrentList();
    private final ResourceEntry missingArgumentResource;

    public ParentCommandContext(Command command, ResourceEntry missingArgumentResource, Class<?> type) {
        super(command, type);
        this.missingArgumentResource = missingArgumentResource;
    }

    public ParentCommandContext(Command command, ResourceEntry missingArgumentResource, Class<?> parent, Method method) {
        super(command, method);
        this.missingArgumentResource = missingArgumentResource;
    }

    @Override
    public Exceptional<ResourceEntry> call(CommandSource source, CommandContext context) {
        Exceptional<AbstractCommandContext> inheritedCommand = this.getParentExecutor();
        inheritedCommand.present(ctx -> ctx.call(source, context));

        if (inheritedCommand.absent()) {
            return super.call(source, context);
        } else {
            return Exceptional.none();
        }
    }

    public Exceptional<AbstractCommandContext> getParentExecutor() {
        return Exceptional.of(this.getInheritedCommands().stream()
                .filter(ctx -> ctx.getAliases().contains(""))
                .findFirst());
    }

    public List<AbstractCommandContext> getInheritedCommands() {
        return this.inheritedCommands;
    }

    public void addInheritedCommand(AbstractCommandContext context) {
        if (null != context) this.inheritedCommands.add(context);
    }
}
