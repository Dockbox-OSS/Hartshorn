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

package com.specific.sub;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;

import javax.inject.Singleton;

import lombok.Getter;

@AutomaticActivation
@Singleton
public class DemoServicePreProcessor implements ServicePreProcessor<Demo> {

    @Getter
    private int processed = 0;

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return key.type().is(DemoService.class);
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        context.log().debug("Processing %s".formatted(key));
        this.processed++;
    }

    @Override
    public Class<Demo> activator() {
        return Demo.class;
    }
}