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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.FakeResource;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(HartshornRunner.class)
public class HashtagParameterPatternTests {

    @Test
    void testPreconditionsAcceptValidPattern() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(CuboidArgument.class, null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertTrue(HartshornUtils.unwrap(result));
    }

    @Test
    void testPreconditionsRejectInvalidPrefix() {
        final String pattern = "@cuboid[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(CuboidArgument.class, null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(HartshornUtils.unwrap(result));
    }

    @Test
    void testPreconditionsRejectInvalidName() {
        final String pattern = "#sphere[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final Exceptional<Boolean> result = parameterPattern.preconditionsMatch(CuboidArgument.class, null, pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertFalse(HartshornUtils.unwrap(result));
    }

    @Test
    void testSplitArgumentsCreatesCorrectArguments() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final List<String> arguments = parameterPattern.splitArguments(pattern);

        Assertions.assertEquals(1, arguments.size());
        Assertions.assertEquals("1", arguments.get(0));
    }

    @Test
    void testIdentifierParser() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final Exceptional<String> identifier = parameterPattern.parseIdentifier(pattern);

        Assertions.assertTrue(identifier.present());
        Assertions.assertEquals("cuboid", identifier.get());
    }

    @Test
    void testValidArgumentCanBeRequested() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.getParameterPattern();

        final Exceptional<CuboidArgument> result = parameterPattern.request(CuboidArgument.class, null, pattern);

        Assertions.assertTrue(result.present());

        final CuboidArgument cuboid = result.get();
        Assertions.assertEquals(1, cuboid.getSize());
    }

    private HashtagParameterPattern getParameterPattern() {
        return new HashtagParameterPattern() {
            @Override
            protected ResourceEntry getWrongFormatResource() {
                // Override resources as these are otherwise requested through wired resource references
                return new FakeResource("failed");
            }
        };
    }
}
