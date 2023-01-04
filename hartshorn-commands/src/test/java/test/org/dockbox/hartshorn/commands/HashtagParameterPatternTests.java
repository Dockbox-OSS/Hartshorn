/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.ConverterException;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern;
import test.org.dockbox.hartshorn.commands.types.CuboidArgument;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.MessageTemplate;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Attempt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import jakarta.inject.Inject;

@UseCommands
@HartshornTest(includeBasePackages = false)
public class HashtagParameterPatternTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testPreconditionsAcceptValidPattern() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Attempt<Boolean, ConverterException> result = parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);

        Assertions.assertTrue(result.present());
        Assertions.assertTrue(result.get());
    }

    private HashtagParameterPattern pattern() {
        return new HashtagParameterPattern() {
            @Override
            protected Message wrongFormat() {
                // Override resources as these are otherwise requested through bound resource references
                return new MessageTemplate("JUnit placeholder: wrong format", "test.failed", Locale.getDefault());
            }
        };
    }

    @Test
    void testPreconditionsRejectInvalidPrefix() {
        final String pattern = "@cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Attempt<Boolean, ConverterException> result = parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result.errorPresent());
    }

    @Test
    void testPreconditionsRejectInvalidName() {
        final String pattern = "#sphere[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Attempt<Boolean, ConverterException> result = parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);

        Assertions.assertTrue(result.absent());
        Assertions.assertTrue(result.errorPresent());
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

        final Attempt<String, ConverterException> identifier = parameterPattern.parseIdentifier(pattern);

        Assertions.assertTrue(identifier.present());
        Assertions.assertEquals("cuboid", identifier.get());
    }

    @Test
    void testValidArgumentCanBeRequested() {
        final String pattern = "#cuboid[1]";
        final CustomParameterPattern parameterPattern = this.pattern();

        final Attempt<CuboidArgument, ConverterException> result = parameterPattern.request(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);

        Assertions.assertTrue(result.present());

        final CuboidArgument cuboid = result.get();
        Assertions.assertEquals(1, cuboid.size());
    }
}

