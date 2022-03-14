/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContextImpl;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.types.CommandValueEnum;
import org.dockbox.hartshorn.commands.types.SampleCommand;
import org.dockbox.hartshorn.commands.types.SampleCommandExtension;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.i18n.annotations.UseTranslations;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.inject.Inject;

@UseCommands
@HartshornTest
@UseTranslations
public class CommandDefinitionContextTests {

    @Inject
    private ApplicationContext applicationContext;

    private final Key<SampleCommand> typeContext = Key.of(SampleCommand.class);

    @Test
    void testParsingCanSucceed() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo sub 1 --skip 1 2 3 4"));
    }

    @Test
    void testExtensionCanSucceed() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        gateway.register(Key.of(SampleCommandExtension.class));
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo second ThisIsMyName"));
    }

    @Test
    void testComplexParsingCanSucceed() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo complex requiredArg optionalArg ONE --flag --vflag flagValue -s"));
    }

    @Test
    void testTooManyArguments() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo complex requiredArg optionalArg ONE thisArgumentIsOneTooMany"));
    }

    @Test
    void testNotEnoughArguments() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo complex")); // Missing required arg (and optional arguments)
    }

    @Test
    void testUnknownFlag() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo complex requiredArg optionalArg ONE --unknownFlag"));
    }

    @Test
    void testArgumentParameters() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.applicationContext), "demo arguments requiredA optionalB --flag valueC"));
    }

    @Test
    void testSpecificSuggestion() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        final List<String> suggestions = gateway.suggestions(SystemSubject.instance(this.applicationContext), "demo complex requiredArg optionalArg O");

        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("one", suggestions.get(0));
    }

    @Test
    void testGroups() throws ParsingException {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        gateway.accept(SystemSubject.instance(this.applicationContext), "demo group");
    }

    @Test
    void testAllSuggestions() {
        final CommandGateway gateway = this.applicationContext.get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        final List<String> suggestions = gateway.suggestions(SystemSubject.instance(this.applicationContext), "demo complex requiredArg optionalArg ");

        Assertions.assertEquals(3, suggestions.size());
        Assertions.assertTrue(suggestions.containsAll(List.of("one", "two", "three")));
    }

    @Test
    void testContainerContext() {
        final Command command = new TestCommand();
        final CommandDefinitionContext context = new CommandDefinitionContextImpl(this.applicationContext, command, null);

        Assertions.assertEquals(1, context.aliases().size());
        Assertions.assertEquals("demo", context.aliases().get(0));

        Assertions.assertEquals(3, context.elements().size());
        Assertions.assertEquals(3, context.flags().size());

        // Below tests also cover element order.

        final CommandElement<?> requiredElement = context.elements().get(0);
        Assertions.assertFalse(requiredElement.optional());
        Assertions.assertEquals("required", requiredElement.name());

        final CommandElement<?> optionalElement = context.elements().get(1);
        Assertions.assertTrue(optionalElement.optional());
        Assertions.assertEquals("optional", optionalElement.name());

        final CommandElement<?> enumElement = context.elements().get(2);
        Assertions.assertTrue(enumElement.optional());
        Assertions.assertEquals("enum", enumElement.name());
        final Exceptional<?> one = enumElement.parse(null, "ONE");
        Assertions.assertTrue(one.present());
        Assertions.assertTrue(one.get() instanceof CommandValueEnum);
        Assertions.assertEquals(CommandValueEnum.ONE, one.get());

        final CommandFlag flag = context.flags().get(0);
        Assertions.assertEquals("flag", flag.name());

        final CommandFlag valueFlag = context.flags().get(1);
        Assertions.assertTrue(valueFlag instanceof CommandElement);
        Assertions.assertEquals("vflag", valueFlag.name());
        Assertions.assertTrue(((CommandElement<?>) valueFlag).optional());

        final CommandFlag shortFlag = context.flags().get(2);
        Assertions.assertEquals("s", shortFlag.name());
    }

    private static class TestCommand implements Command {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Command.class;
        }

        @Override
        public String[] value() {
            return new String[]{ "demo" };
        }

        @Override
        public String arguments() {
            return "<required{String}> [optional{String}]  [enum{org.dockbox.hartshorn.commands.types.CommandValueEnum}] --flag --vflag String -s";
        }

        @Override
        public Class<?> parent() {
            return Void.class;
        }

        @Override
        public boolean lazy() {
            return false;
        }
    }
}
