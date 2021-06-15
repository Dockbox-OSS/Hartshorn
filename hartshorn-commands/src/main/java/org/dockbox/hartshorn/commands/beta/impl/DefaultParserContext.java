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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.commands.beta.api.ParserContext;
import org.dockbox.hartshorn.commands.context.CommandParameter;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class DefaultParserContext extends DefaultContext implements ParserContext {

    private final List<CommandParameter<?>> arguments;
    private final List<CommandParameter<?>> flags;
    private final String alias;

    @Override
    public List<CommandParameter<?>> arguments() {
        return HartshornUtils.asUnmodifiableList(this.arguments);
    }

    @Override
    public List<CommandParameter<?>> flags() {
        return HartshornUtils.asUnmodifiableList(this.flags);
    }

    @Override
    public String alias() {
        return this.alias;
    }
}
