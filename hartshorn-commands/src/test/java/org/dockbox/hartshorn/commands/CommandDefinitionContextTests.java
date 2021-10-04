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
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContextImpl;
import org.dockbox.hartshorn.commands.definition.CommandElement;
import org.dockbox.hartshorn.commands.definition.CommandFlag;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.commands.types.CommandValueEnum;
import org.dockbox.hartshorn.commands.types.SampleCommand;
import org.dockbox.hartshorn.commands.types.SampleCommandExtension;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

public class CommandDefinitionContextTests extends ApplicationAwareTest {

    private final TypeContext<SampleCommand> typeContext = TypeContext.of(SampleCommand.class);

    @Test
    void testParsingCanSucceed() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.context()), "demo sub 1 --skip 1 2 3 4"));
    }

    @Test
    void testExtensionCanSucceed() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        gateway.register(TypeContext.of(SampleCommandExtension.class));
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.context()), "demo second ThisIsMyName"));
    }

    @Test
    void testComplexParsingCanSucceed() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.context()), "demo complex requiredArg optionalArg ONE --flag --vflag flagValue -s"));
    }

    @Test
    void testTooManyArguments() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.context()), "demo complex requiredArg optionalArg ONE thisArgumentIsOneTooMany"));
    }

    @Test
    void testNotEnoughArguments() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.context()), "demo complex")); // Missing required arg (and optional arguments)
    }

    @Test
    void testUnknownFlag() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertThrows(ParsingException.class, () -> gateway.accept(SystemSubject.instance(this.context()), "demo complex requiredArg optionalArg ONE --unknownFlag"));
    }

    @Test
    void testArgumentParameters() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        Assertions.assertDoesNotThrow(() -> gateway.accept(SystemSubject.instance(this.context()), "demo arguments requiredA optionalB --flag valueC"));
    }

    @Test
    void testSpecificSuggestion() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        final List<String> suggestions = gateway.suggestions(SystemSubject.instance(this.context()), "demo complex requiredArg optionalArg O");

        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("one", suggestions.get(0));
    }

    @Test
    void testGroups() throws ParsingException {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        gateway.accept(SystemSubject.instance(this.context()), "demo group");
    }

    @Test
    void testAllSuggestions() {
        final CommandGateway gateway = this.context().get(CommandGatewayImpl.class);
        gateway.register(this.typeContext);
        final List<String> suggestions = gateway.suggestions(SystemSubject.instance(this.context()), "demo complex requiredArg optionalArg ");

        Assertions.assertEquals(3, suggestions.size());
        Assertions.assertTrue(suggestions.containsAll(HartshornUtils.asList("one", "two", "three")));
    }

    @Test
    void testContainerContext() {
        final Command command = this.createCommand();
        final CommandDefinitionContext context = new CommandDefinitionContextImpl(this.context(), command, null);

        Assertions.assertEquals("demo", context.permission().get());
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

    private Command createCommand() {
        //noinspection OverlyComplexAnonymousInnerClass
        return new Command() {

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
            public String permission() {
                return "demo";
            }

            @Override
            public Class<?> parent() {
                return Void.class;
            }
        };
    }
}
