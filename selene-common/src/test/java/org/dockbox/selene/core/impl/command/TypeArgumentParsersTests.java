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

package org.dockbox.selene.core.impl.command;

import org.dockbox.selene.core.command.context.CommandValue;
import org.dockbox.selene.core.impl.command.convert.TypeArgumentParsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TypeArgumentParsersTests {

    @Test
    public void parseSizeEquals() {
        List<Integer> list = this.generateList();
        Assertions.assertSame(3, list.size());
    }

    @Test
    public void parseArgumentsMatch() {
        List<Integer> list = this.generateList();
        Assertions.assertSame(1, list.get(0));
        Assertions.assertSame(2, list.get(1));
        Assertions.assertSame(3, list.get(2));
    }

    @Test
    public void parseDelimiterWorks() {
        CommandValue<String> commandvalue = new CommandValue.Argument<>("1:2:3", "mock_arg");
        TypeArgumentParsers.ListParser<Integer> parser = new TypeArgumentParsers.ListParser<>(Integer::parseInt);
        parser.setDelimiter(':');

        List<Integer> list = parser.parse(commandvalue).get();
        Assertions.assertSame(3, list.size());
    }

    private List<Integer> generateList() {
        CommandValue<String> commandvalue = new CommandValue.Argument<>("1,2,3", "mock_arg");
        TypeArgumentParsers.ListParser<Integer> parser = new TypeArgumentParsers.ListParser<>(Integer::parseInt);

        return parser.parse(commandvalue).get();
    }

}
