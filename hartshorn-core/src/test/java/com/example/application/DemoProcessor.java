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

package com.example.application;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
@AutomaticActivation
public class DemoProcessor implements ComponentPreProcessor<UseDemo> {

    @Inject
    @Getter
    private DemoService demoService;

    @Inject
    @Getter
    private Demo demo;

    @Override
    public Class<UseDemo> activator() {
        return UseDemo.class;
    }

    @Override
    public boolean modifies(final ApplicationContext context, final Key<?> key) {
        return false;
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
    }
}
