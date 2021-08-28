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
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts prefixed patterns into type instances used by command executors. The
 * pattern is decided on by any implementation of this type.
 */
public abstract class PrefixedParameterPattern implements CustomParameterPattern {

    @Override
    public <T> Exceptional<Boolean> preconditionsMatch(final TypeContext<T> type, final CommandSource source, final String raw) {
        return Exceptional.of(() -> {
                    String prefix = this.prefix() + "";
                    if (this.requiresTypeName()) {
                        final String parameterName = type.annotation(Parameter.class).get().value();
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
    public List<String> splitArguments(final String raw) {
        final String group = raw.substring(raw.indexOf(this.opening()));
        final List<String> arguments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int openCount = 0;
        for (final char c : group.toCharArray()) {
            current.append(c);
            if (this.opening() == c) {
                openCount++;
            }
            else if (this.closing() == c) {
                openCount--;
                if (0 == openCount) {
                    final String out = current.toString();
                    arguments.add(out.substring(1, out.length() - 1));
                    current = new StringBuilder();
                }
            }
        }
        return arguments;
    }

    @Override
    public Exceptional<String> parseIdentifier(final String argument) {
        return Exceptional.of(() -> argument.startsWith(this.prefix() + ""),
                () -> argument.substring(1, argument.indexOf(this.opening())),
                () -> new IllegalArgumentException(this.wrongFormat().asString())
        );
    }

    /**
     * The opening character of a new argument.
     *
     * @return The character
     */
    protected abstract char opening();

    /**
     * The closing character of a argument.
     *
     * @return The character
     */
    protected abstract char closing();

    /**
     * The prefix indicating a new type argument.
     *
     * @return The character
     */
    protected abstract char prefix();

    /**
     * Whether the pattern requires the name of the type to be present.
     *
     * @return <code>true</code> if the name is required, else <code>false</code>
     */
    protected abstract boolean requiresTypeName();

    /**
     * The resource to send to the {@link CommandSource} when a argument is not formatted correctly.
     *
     * @return The resource
     */
    protected abstract ResourceEntry wrongFormat();
}
