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

package org.dockbox.selene.di.modules;

import com.google.inject.AbstractModule;

import java.lang.annotation.Annotation;

public class SingleAnnotatedImplementationModule<T> extends AbstractModule {

    private final Class<T> target;
    private final Class<? extends T> implementation;
    private final Class<? extends Annotation> annotation;

    public SingleAnnotatedImplementationModule(Class<T> target, Class<? extends T> implementation, Class<? extends Annotation> annotation) {
        this.target = target;
        this.implementation = implementation;
        this.annotation = annotation;
    }

    @Override
    protected void configure() {
        super.configure();
        this.bind(this.target).annotatedWith(this.annotation).to(this.implementation);
    }
}
