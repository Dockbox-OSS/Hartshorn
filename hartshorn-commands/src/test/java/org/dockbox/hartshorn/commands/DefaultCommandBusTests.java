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
import org.dockbox.hartshorn.commands.registration.AbstractRegistrationContext;
import org.dockbox.hartshorn.commands.registration.CommandInheritanceContext;
import org.dockbox.hartshorn.commands.registration.MethodCommandContext;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

@ExtendWith(HartshornRunner.class)
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

    @Command({ "example", "sample" })
    private static class ExampleCommandClass {

        @Command
        public void mainCommand() {}

        @Command("child")
        public void subCommand() {}

        @Command(value = "noninherit", inherit = false)
        public void nonInheritedChild() {}
    }

    @Command(value = "sample", extend = true)
    private static class ExampleExtendingCommandClass {

        @Command("extended")
        public void extendedCommand() {}
    }
}
