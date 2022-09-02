/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.i18n.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts prefixed patterns into type instances used by command executors. The
 * pattern is decided on by any implementation of this type.
 */
public abstract class PrefixedParameterPattern implements CustomParameterPattern {

    @Override
    public <T> Result<Boolean> preconditionsMatch(final TypeContext<T> type, final CommandSource source, final String raw) {
        String prefix = this.prefix() + "";
        if (this.requiresTypeName()) {
            final String parameterName = type.annotation(Parameter.class).get().value();
            prefix = this.prefix() + parameterName;
        }
        if (raw.startsWith(prefix)) {
            return Result.of(true);
        } else {
            return Result.of(new ArgumentMatchingFailedException(this.wrongFormat()));
        }
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
    public Result<String> parseIdentifier(final String argument) {
        if (argument.startsWith(this.prefix() + "")) {
            return Result.of(argument.substring(1, argument.indexOf(this.opening())));
        }
        else {
            return Result.of(new ArgumentMatchingFailedException(this.wrongFormat()));
        }
    }

    /**
     * The opening character of a new argument.
     *
     * @return The character
     */
    protected abstract char opening();

    /**
     * The closing character of an argument.
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
     * The resource to send to the {@link CommandSource} when an argument is not formatted correctly.
     *
     * @return The resource
     */
    protected abstract Message wrongFormat();
}
