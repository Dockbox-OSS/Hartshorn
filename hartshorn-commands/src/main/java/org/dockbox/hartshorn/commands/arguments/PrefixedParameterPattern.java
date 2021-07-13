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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.util.Reflect;

import java.util.ArrayList;
import java.util.List;

public abstract class PrefixedParameterPattern implements CustomParameterPattern {

    @Override
    public <T> Exceptional<Boolean> preconditionsMatch(Class<T> type, CommandSource source, String raw) {
        return Exceptional.of(() -> {
                    String prefix = this.prefix() + "";
                    if (this.requiresTypeName()) {
                        String parameterName = Reflect.annotation(type, Parameter.class).get().value();
                        prefix = this.prefix() + parameterName;
                    }
                    return raw.startsWith(prefix);
                },
                () -> true,
                () -> false,
                () -> new IllegalArgumentException(this.wrongFormat().asString())
        );
    }

    @Override
    public List<String> splitArguments(String raw) {
        String group = raw.substring(raw.indexOf(this.opening()));
        List<String> arguments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int openCount = 0;
        for (char c : group.toCharArray()) {
            current.append(c);
            if (this.opening() == c) {
                openCount++;
            }
            else if (this.closing() == c) {
                openCount--;
                if (0 == openCount) {
                    String out = current.toString();
                    arguments.add(out.substring(1, out.length() - 1));
                    current = new StringBuilder();
                }
            }
        }
        return arguments;
    }

    @Override
    public Exceptional<String> parseIdentifier(String argument) {
        return Exceptional.of(() -> argument.startsWith(this.prefix() + ""),
                () -> argument.substring(1, argument.indexOf(this.opening())),
                () -> new IllegalArgumentException(this.wrongFormat().asString())
        );
    }

    protected abstract char opening();

    protected abstract char closing();

    protected abstract char prefix();

    protected abstract boolean requiresTypeName();

    protected abstract ResourceEntry wrongFormat();
}
