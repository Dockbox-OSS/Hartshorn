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

package org.dockbox.selene.core.command.context;

import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.command.context.CommandValue.Flag;
import org.dockbox.selene.core.command.context.CommandValue.Type;
import org.dockbox.selene.core.command.parsing.TypeParser;
import org.dockbox.selene.core.objects.Exceptional;

public interface CommandContext {

    String getAlias();
    int getArgumentCount();
    int getFlagCount();

    Exceptional<Argument<String>> getArgument(String key);
    <T> Exceptional<Argument<T>> getArgument(String key, Class<T> type);
    <T> Exceptional<T> getArgumentAndParse(String key, TypeParser<T> parser);


    Exceptional<Flag<String>> getFlag(String key);
    <T> Exceptional<Flag<T>> getFlag(String key, Class<T> type);
    <T> Exceptional<T> getFlagAndParse(String key, TypeParser<T> parser);

    boolean hasArgument(String key);
    boolean hasFlag(String key);

    <T> Exceptional<CommandValue<T>> getValue(String key, Class<T> type, Type valueType);
    <T> Exceptional<T> tryCreate(Class<T> type);

}
