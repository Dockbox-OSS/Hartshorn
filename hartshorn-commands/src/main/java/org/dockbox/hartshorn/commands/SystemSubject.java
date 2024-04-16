/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.Locale;
import java.util.UUID;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.Identifiable;

import jakarta.inject.Inject;

public abstract class SystemSubject implements CommandSource, Identifiable {

    private final ApplicationContext applicationContext;
    private Locale locale = Locale.getDefault();

    public static final UUID UNIQUE_ID = new UUID(0, 0);

    public static SystemSubject instance(ApplicationContext context) {
        return context.get(SystemSubject.class);
    }

    @Inject
    protected SystemSubject(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public Locale language() {
        return this.locale;
    }

    @Override
    public void language(Locale language) {
        this.locale = language;
    }

    @Override
    public UUID uniqueId() {
        return UNIQUE_ID;
    }

    @Override
    public String name() {
        return "System";
    }
}
