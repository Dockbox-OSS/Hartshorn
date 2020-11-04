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

package org.dockbox.selene.core.server;

import com.google.inject.AbstractModule;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

final class ExtensionModule extends AbstractModule {

    private static final class InternalBinding<T> {

        private final Class<T> sourceClass;
        private final T instance;

        private InternalBinding(Class<T> sourceClass, T instance) {
            this.sourceClass = sourceClass;
            this.instance = instance;
        }

        public Class<T> getSourceClass() {
            return this.sourceClass;
        }

        public T getInstance() {
            return this.instance;
        }
    }

    private final Collection<InternalBinding<Object>> bindings = new CopyOnWriteArrayList<>();

    @Override
    protected void configure() {
        this.bindings.forEach(binding ->
                this.bind(binding.getSourceClass()).toInstance(binding.getInstance()));
    }

    @SuppressWarnings("unchecked")
    public <T> void acceptBinding(Class<T> sourceClass, T instance) {
        InternalBinding<T> binding = new InternalBinding<>(sourceClass, instance);
        this.bindings.add((InternalBinding<Object>) binding);
    }

}
