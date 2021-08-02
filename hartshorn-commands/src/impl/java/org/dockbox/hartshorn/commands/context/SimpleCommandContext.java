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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple implementation of {@link CommandContext}.
 */
@SuppressWarnings("unchecked")
@AllArgsConstructor
public class SimpleCommandContext extends DefaultContext implements CommandContext {

    private final String command;
    private final List<CommandParameter<?>> args;
    private final List<CommandParameter<?>> flags;

    @Getter
    private final CommandSource source;
    @Getter(onMethod_ = @UnmodifiableView)
    private final List<Permission> permissions;

    @Override
    public <T> T get(String key) {
        return HartshornUtils.merge(this.args, this.flags)
                .stream()
                .map(CommandParameter.class::cast)
                .filter(arg -> arg.trimmedKey().equals(key))
                .findFirst()
                .map(arg -> (T) arg.value())
                .orElse(null);
    }

    @Override
    public boolean has(String key) {
        return HartshornUtils.merge(this.args, this.flags)
                .stream()
                .map(CommandParameter.class::cast)
                .anyMatch(arg -> arg.trimmedKey().equals(key));
    }

    @Override
    public <T> Exceptional<T> find(String key) {
        return Exceptional.of(() -> this.get(key));
    }

    @Override
    public <T> Exceptional<CommandParameter<T>> argument(String key) {
        return Exceptional.of(this.args.stream()
                .filter(arg -> arg.trimmedKey().equals(key))
                .findFirst()
        ).map(arg -> (CommandParameter<T>) arg);
    }

    @Override
    public <T> Exceptional<CommandParameter<T>> flag(String key) {
        return Exceptional.of(this.flags.stream()
                .filter(flag -> flag.trimmedKey().equals(key))
                .findFirst()
        ).map(flag -> (CommandParameter<T>) flag);
    }

    @Override
    public String command() {
        return this.command;
    }

    @Override
    @UnmodifiableView
    public List<CommandParameter<?>> arguments() {
        return HartshornUtils.asUnmodifiableList(this.args);
    }

    @Override
    @UnmodifiableView
    public List<CommandParameter<?>> flags() {
        return HartshornUtils.asUnmodifiableList(this.flags);
    }

    @Override
    public String alias() {
        return this.command.split(" ")[0];
    }
}
