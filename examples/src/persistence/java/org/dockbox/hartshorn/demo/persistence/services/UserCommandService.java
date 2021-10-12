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

package org.dockbox.hartshorn.demo.persistence.services;

import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContextImpl;
import org.dockbox.hartshorn.di.annotations.service.Service;

import javax.inject.Inject;

/**
 * A simple capable of handling commands. Any type annotated with {@link Service} (or an
 * extension of it) is automatically registered to the {@link org.dockbox.hartshorn.commands.CommandGateway}
 * if there are methods annotated with {@link Command}.
 */
@Service
public class UserCommandService {

    @Inject
    private UserPersistence persistenceService;

    /**
     * The method activated when the command {@code create <name> <age>} is correctly entered by a user
     * (or other {@link java.io.InputStream}, depending on the {@link org.dockbox.hartshorn.commands.CommandCLI}).
     *
     * <p>The {@link Command#value()} indicates the command itself, excluding arguments. {@link Command#arguments()}
     * indicates the arguments which are expected to be present. The way these are defined depends on the {@link org.dockbox.hartshorn.commands.CommandParser}
     * which is used in the {@link org.dockbox.hartshorn.commands.CommandGateway}. By default this uses the {@link org.dockbox.hartshorn.commands.CommandParserImpl},
     * which uses the definition context defined in {@link CommandDefinitionContextImpl}.
     *
     * @see CommandDefinitionContextImpl
     */
    @Command(value = "create", arguments = "<name> <age{Int}>")
    public void create(CommandContext context) {
        String name = context.get("name");
        int age = context.get("age");
        this.persistenceService.createUser(name, age);
    }

}
