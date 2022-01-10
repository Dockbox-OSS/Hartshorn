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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern;
import org.dockbox.hartshorn.commands.types.CuboidArgument;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.MessageTemplate;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import lombok.Getter;

@UseCommands
@HartshornTest
public class HashtagParameterPatternTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Test
    void testPreconditionsAcceptValidPattern() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(TypeContext.of(CuboidArgument.class), null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertTrue(result.get());
    }

    private HashtagParameterPattern pattern() {
        return new HashtagParameterPattern() {
            @Override
            protected Message wrongFormat() {
                // Override resources as these are otherwise requested through bound resource references
                return new MessageTemplate("failed", "test.failed", Locale.getDefault());
            }
        };
    }

    @Test
    void testPreconditionsRejectInvalidPrefix() {
        final String pattern = "@cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(TypeContext.of(CuboidArgument.class), null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.get());
    }

    @Test
    void testPreconditionsRejectInvalidName() {
        final String pattern = "#sphere[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(TypeContext.of(CuboidArgument.class), null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(result.get());
    }

    @Test
    void testSplitArgumentsCreatesCorrectArguments() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final List<String> arguments = parameterPattern.splitArguments(pattern);

        Assertions.assertEquals(1, arguments.size());
        Assertions.assertEquals("1", arguments.get(0));
    }

    @Test
    void testIdentifierParser() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Exceptional<String> identifier = parameterPattern.parseIdentifier(pattern);

        Assertions.assertTrue(identifier.present());
        Assertions.assertEquals("cuboid", identifier.get());
    }

    @Test
    void testValidArgumentCanBeRequested() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Exceptional<CuboidArgument> result = parameterPattern.request(TypeContext.of(CuboidArgument.class), SystemSubject.instance(this.applicationContext()), pattern);

        Assertions.assertTrue(result.present());

        final CuboidArgument cuboid = result.get();
        Assertions.assertEquals(1, cuboid.size());
    }
}

