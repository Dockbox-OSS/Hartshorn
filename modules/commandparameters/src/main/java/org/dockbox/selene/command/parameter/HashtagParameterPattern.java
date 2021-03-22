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

package org.dockbox.selene.command.parameter;

import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.commandparameters.CommandParameterResources;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class HashtagParameterPattern implements CustomParameterPattern {

    /*
     * Scans for arguments wrapped in square brackets. Matches anything that is not a square bracket on its own. E.g.:
     * - [arg1] matches, 1 group [arg1]
     * - [arg1][arg2] matches, 2 groups [arg1] and [arg2]
     * - [[]] does not match
     * - [[arg1]] matches, 1 group [arg1]
     */
    private static final Pattern ARGUMENT = Pattern.compile("\\[[^\\[\\]]+\\]");

    @Override
    public <T> Exceptional<Boolean> preconditionsMatch(Class<T> type, CommandSource source, String raw) {
        return Exceptional.of(() -> raw.startsWith("#"),
                () -> true,
                () -> new IllegalArgumentException(CommandParameterResources.HASHTAG_PATTERN_WRONG_FORMAT.asString())
        );
    }

    @Override
    public List<String> splitArguments(String raw) {
        // TODO: Fix this so #cuboid[pyramid][#shape[triangle][4]] splits into 'pyramid' and '#shape[triangle][4]' instead of 'pyramid', 'triangle', '4'
        List<String> rawArguments = SeleneUtils.emptyList();
        Matcher matcher = ARGUMENT.matcher(raw);
        while (matcher.find()) {
            String argument = matcher.group();
            argument = argument.substring(1, argument.length() - 1);
            rawArguments.add(argument);
        }
        return rawArguments;
    }

    @Override
    public Exceptional<String> parseIdentifier(String argument) {
        return Exceptional.of(() -> argument.startsWith("#"),
                () -> argument.substring(1, argument.indexOf("[")),
                () -> new IllegalArgumentException(CommandParameterResources.HASHTAG_PATTERN_WRONG_FORMAT.asString())
        );
    }
}
