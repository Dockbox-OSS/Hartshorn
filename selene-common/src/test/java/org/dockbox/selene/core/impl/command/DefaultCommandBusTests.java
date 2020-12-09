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

package org.dockbox.selene.core.impl.command;

import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.impl.command.registration.AbstractRegistrationContext;
import org.dockbox.selene.core.impl.command.registration.CommandInheritanceContext;
import org.dockbox.selene.core.impl.command.registration.MethodCommandContext;
import org.dockbox.selene.test.util.TestCommandBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class DefaultCommandBusTests {

    private static final DefaultCommandBus bus = new TestCommandBus();

    @BeforeAll
    static void prepareRegistration() {
        bus.register(
                ExampleCommandClass.class,
                ExampleExtendingCommandClass.class
        );
    }

    @Test
    public void extendedCommandIsPresent() {
        AbstractRegistrationContext context = bus.getRegistrations().get("sample");
        Assertions.assertTrue(context instanceof CommandInheritanceContext);

        CommandInheritanceContext inheritanceContext = (CommandInheritanceContext) context;
        Optional<MethodCommandContext> extendedContextOptional = inheritanceContext.getInheritedCommands().stream()
                .filter(inheritedContext -> inheritedContext.getAliases().contains("extended"))
                .findFirst();

        Optional<MethodCommandContext> contextOptional = inheritanceContext.getInheritedCommands().stream()
                .filter(inheritedContext -> inheritedContext.getAliases().contains("noargs"))
                .findFirst();

        Assertions.assertTrue(extendedContextOptional.isPresent());
        Assertions.assertTrue(contextOptional.isPresent());
    }

    @Command(aliases = {"example", "sample"}, usage = "example")
    private static class ExampleCommandClass {

        @Command(aliases = "", usage = "")
        public void mainCommand() { }

        @Command(aliases = "optionalarg", usage = "optionalarg [argument]")
        public void childWithOptionalArgument() { }

        @Command(aliases = "requiredarg", usage = "requiredarg <argument>")
        public void childWithRequiredArgument() { }

        @Command(aliases = "noargs", usage = "noargs")
        public void childWithoutArgument() { }

        @Command(aliases = "noninherit", usage = "noninherit", inherit = false)
        public void nonInheritedChild() { }

    }

    @Command(aliases = "sample", usage = "sample", extend = true)
    private static class ExampleExtendingCommandClass {

        @Command(aliases = "extended", usage = "extended")
        public void extendedCommand() { }

    }

}
