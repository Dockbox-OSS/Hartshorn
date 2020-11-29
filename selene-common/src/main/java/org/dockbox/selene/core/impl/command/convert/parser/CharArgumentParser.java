/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.command.convert.parser;

import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.jetbrains.annotations.NotNull;

public class CharArgumentParser extends AbstractTypeArgumentParser<Character> {
    @NotNull
    @Override
    public Exceptional<Character> parse(@NotNull CommandValue<String> commandValue) {
        int length = commandValue.getValue().length();
        return 1 == length ? Exceptional.of(commandValue.getValue().charAt(0)) : Exceptional.empty();
    }
}
