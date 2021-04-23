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

package org.dockbox.selene.commands;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.registration.AbstractRegistrationContext;
import org.dockbox.selene.commands.registration.CommandInheritanceContext;
import org.dockbox.selene.commands.registration.MethodCommandContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class DefaultCommandBusTests {

    private static final CommandBus bus = new TestCommandBus();

    @BeforeAll
    static void prepareRegistration() {
        bus.register(ExampleCommandClass.class, ExampleExtendingCommandClass.class);
    }

    @Test
    public void extendedCommandIsPresent() {
        AbstractRegistrationContext context = DefaultCommandBus.getRegistrations().get("sample");
        Assertions.assertTrue(context instanceof CommandInheritanceContext);

        CommandInheritanceContext inheritanceContext = (CommandInheritanceContext) context;
        Optional<MethodCommandContext> extendedContextOptional = inheritanceContext.getInheritedCommands().stream()
                .filter(inheritedContext -> inheritedContext.getAliases().contains("extended"))
                .findFirst();

        Optional<MethodCommandContext> contextOptional = inheritanceContext.getInheritedCommands().stream()
                .filter(inheritedContext -> inheritedContext.getAliases().contains("child"))
                .findFirst();

        Assertions.assertTrue(extendedContextOptional.isPresent());
        Assertions.assertTrue(contextOptional.isPresent());
    }

    @Test
    public void parentCommandIsPresent() {
        AbstractRegistrationContext context = DefaultCommandBus.getRegistrations().get("example");
        Assertions.assertTrue(context instanceof CommandInheritanceContext);

        CommandInheritanceContext inheritanceContext = (CommandInheritanceContext) context;
        Exceptional<MethodCommandContext> parentContext = inheritanceContext.getParentExecutor();
        Assertions.assertTrue(parentContext.present());
    }

    @Test
    public void nonInheritedCommandIsPresent() {
        AbstractRegistrationContext context = DefaultCommandBus.getRegistrations().get("noninherit");
        Assertions.assertTrue(context instanceof MethodCommandContext);
    }

    @Test
    public void nonInheritedCommandIsNotPresentInParent() {
        AbstractRegistrationContext sampleContext = DefaultCommandBus.getRegistrations().get("sample");
        Assertions.assertTrue(sampleContext instanceof CommandInheritanceContext);

        CommandInheritanceContext inheritanceContext = (CommandInheritanceContext) sampleContext;
        Optional<MethodCommandContext> inheritedCommandContext = inheritanceContext.getInheritedCommands().stream()
                .filter(inheritedContext -> inheritedContext.getAliases().contains("noninherit"))
                .findFirst();
        Assertions.assertFalse(inheritedCommandContext.isPresent());
    }

    @Command(
            aliases = { "example", "sample" },
            usage = "example", permission = "example")
    private static class ExampleCommandClass {

        @Command(aliases = "", usage = "", permission = "example")
        public void mainCommand() {}

        @Command(aliases = "child", usage = "child", permission = "example")
        public void subCommand() {}

        @Command(aliases = "noninherit", usage = "noninherit", inherit = false, permission = "example")
        public void nonInheritedChild() {}
    }

    @Command(aliases = "sample", usage = "sample", extend = true, permission = "example")
    private static class ExampleExtendingCommandClass {

        @Command(aliases = "extended", usage = "extended", permission = "example")
        public void extendedCommand() {}
    }
}
