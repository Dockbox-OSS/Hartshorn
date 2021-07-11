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

import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.di.annotations.inject.Wired;

/**
 * Converts Hashtag-patterns into type instances used by command executors. The pattern follows the HashtagPatternParser from WorldEdit.
 * Patterns are expected to start with a hashtag (#) followed by the alias of the type which is ignored while parsing (due to the
 * availability of the Class for the type. Arguments are to be surrounded with square brackets.
 *
 * <p>An example of this pattern is as follows: if we have a constructor for a Shape type
 * <pre>{@code
 * public Shape(String name, int sides) { ... }
 * }</pre>
 * The pattern for this type is expected to be <pre>#shape[square][4]</pre>
 */
public class HashtagParameterPattern extends PrefixedParameterPattern {

    @Wired
    private CommandParameterResources resources;

    @Override
    protected char getOpening() {
        return '[';
    }

    @Override
    protected char getClosing() {
        return ']';
    }

    @Override
    protected char getPrefix() {
        return '#';
    }

    @Override
    protected boolean requiresTypeName() {
        return true;
    }

    @Override
    protected ResourceEntry getWrongFormatResource() {
        return this.resources.getWrongHashtagPatternFormat();
    }
}
