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

import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Identifiable;

import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public abstract class SystemSubject implements CommandSource, Identifiable {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    public static final UUID UNIQUE_ID = new UUID(0, 0);

    public static SystemSubject instance(final ApplicationContext context) {
        return context.get(SystemSubject.class);
    }

    @Override
    public Locale language() {
        return Locale.getDefault();
    }

    @Override
    public void language(final Locale language) {
        // Nothing happens
    }

    @Override
    public UUID uniqueId() {
        return UNIQUE_ID;
    }

    @Override
    public String name() {
        return "System";
    }

    @Override
    public void execute(final String command) {
        try {
            this.applicationContext.get(CommandGateway.class).accept(this, command);
        }
        catch (final ParsingException e) {
            this.applicationContext.handle(e);
        }
    }
}
