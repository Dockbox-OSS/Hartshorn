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

package org.dockbox.hartshorn.demo.persistence.services;

import org.dockbox.hartshorn.commands.CommandListener;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.CommandDefinitionContextImpl;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.demo.persistence.domain.User;

import javax.inject.Inject;

/**
 * A simple capable of handling commands. Any type annotated with {@link Service} (or an
 * extension of it) is automatically registered to the {@link org.dockbox.hartshorn.commands.CommandGateway}
 * if there are methods annotated with {@link Command}.
 */
@Service
public class UserCommandService {

    @Inject
    private UserRepository persistenceService;

    /**
     * The method activated when the command {@code create <name> <age>} is correctly entered by a user
     * (or other {@link java.io.InputStream}, depending on the {@link CommandListener}).
     *
     * <p>The {@link Command#value()} indicates the command itself, excluding arguments. {@link Command#arguments()}
     * indicates the arguments which are expected to be present. The way these are defined depends on the {@link org.dockbox.hartshorn.commands.CommandParser}
     * which is used in the {@link org.dockbox.hartshorn.commands.CommandGateway}. By default, this uses the {@link org.dockbox.hartshorn.commands.CommandParserImpl},
     * which uses the definition context defined in {@link CommandDefinitionContextImpl}.
     *
     * @see CommandDefinitionContextImpl
     */
    @Command(value = "create", arguments = "<name> <age{Int}>")
    public void create(final CommandContext context) {
        final String name = context.get("name");
        final int age = context.get("age");
        this.persistenceService.save(new User(name, age));
    }

}
