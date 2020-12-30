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

package org.dockbox.selene.core.command.parsing;

import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.objects.Exceptional;

public abstract class ArgumentParser extends AbstractParser {

    /**
     * The method used to parse {@link CommandValue CommandValues} into the given
     * generic type.
     *
     * @param <A>
     *         the generic type to convert to
     * @param commandValue
     *         the {@link CommandValue} in String format to parse.
     * @param type
     *         the type to convert to
     *
     * @return the optional type of the generic type. Should return {@link Exceptional#empty()} if
     *         null or if the value could not be parsed.
     */
    public abstract <A> Exceptional<A> parse(CommandValue<String> commandValue, Class<A> type);
}
