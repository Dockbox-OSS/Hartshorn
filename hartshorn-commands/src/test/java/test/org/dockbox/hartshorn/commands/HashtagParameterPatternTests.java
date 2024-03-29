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

import java.util.List;
import java.util.Locale;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandParameterResources;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.ConverterException;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.HashtagParameterPattern;
import org.dockbox.hartshorn.commands.arguments.PrefixedParameterPattern;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.MessageTemplate;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.commands.types.CuboidArgument;

@UseCommands
@HartshornTest(includeBasePackages = false)
public class HashtagParameterPatternTests {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ArgumentConverterRegistry registry;

    @Inject
    private CommandParameterResources resources;

    @Test
    void testPreconditionsAcceptValidPattern() {
        String pattern = "#cuboid[1]";
        PrefixedParameterPattern parameterPattern = this.pattern();

        boolean result = parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);
        Assertions.assertTrue(result);
    }

    private PrefixedParameterPattern pattern() {
        return new HashtagParameterPattern(registry, resources) {
            @Override
            protected Message wrongFormat() {
                // Override resources as these are otherwise requested through bound resource references
                return new MessageTemplate("JUnit placeholder: wrong format", "test.failed", Locale.getDefault());
            }
        };
    }

    @Test
    void testPreconditionsRejectInvalidPrefix() {
        String pattern = "@cuboid[1]";
        PrefixedParameterPattern parameterPattern = this.pattern();

        Assertions.assertThrows(ConverterException.class, () -> {
            parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);
        });
    }

    @Test
    void testPreconditionsRejectInvalidName() {
        String pattern = "#sphere[1]";
        PrefixedParameterPattern parameterPattern = this.pattern();

        Assertions.assertThrows(ConverterException.class, () -> {
            parameterPattern.preconditionsMatch(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);
        });
    }

    @Test
    void testSplitArgumentsCreatesCorrectArguments() {
        String pattern = "#cuboid[1]";
        PrefixedParameterPattern parameterPattern = this.pattern();

        List<String> arguments = parameterPattern.splitArguments(pattern);

        Assertions.assertEquals(1, arguments.size());
        Assertions.assertEquals("1", arguments.get(0));
    }

    @Test
    void testIdentifierParser() {
        String pattern = "#cuboid[1]";
        PrefixedParameterPattern parameterPattern = this.pattern();

        Option<String> identifier = parameterPattern.parseIdentifier(pattern);

        Assertions.assertTrue(identifier.present());
        Assertions.assertEquals("cuboid", identifier.get());
    }

    @Test
    void testValidArgumentCanBeRequested() {
        String pattern = "#cuboid[1]";
        CustomParameterPattern parameterPattern = this.pattern();

        Option<CuboidArgument> result = parameterPattern.request(CuboidArgument.class, SystemSubject.instance(this.applicationContext), pattern);

        Assertions.assertTrue(result.present());

        CuboidArgument cuboid = result.get();
        Assertions.assertEquals(1, cuboid.size());
    }
}

