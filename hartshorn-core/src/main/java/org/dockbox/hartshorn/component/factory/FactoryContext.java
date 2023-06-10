/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The context used by the {@link FactoryServicePreProcessor} and {@link FactoryServicePostProcessor}. This context keeps track
 * of associated constructors for {@link Factory} methods. If no constructor
 * exists for a given method, an exception will be thrown by this context.
 *
 * @deprecated See {@link Factory}.
 */
@Deprecated(since = "23.1", forRemoval = true)
@InstallIfAbsent
public class FactoryContext extends DefaultProvisionContext {

    private final Map<MethodView<?, ?>, ConstructorView<?>> bounds = new ConcurrentHashMap<>();

    /**
     * Associates a constructor with a method.
     *
     * @param method The method to associate the constructor with.
     * @param constructor The constructor to associate with the method.
     * @param <T> The return type of the method.
     */
    public <T> void register(final MethodView<T, ?> method, final ConstructorView<T> constructor) {
        this.bounds.put(method, constructor);
    }

    /**
     * Returns the constructor associated with the given method. If no constructor is associated with the method, an
     * exception will be thrown.
     *
     * @param method The method to get the constructor for.
     * @param <T> The return type of the method.
     * @return The constructor associated with the given method.
     * @throws NoSuchElementException If no constructor is associated with the method.
     */
    public <T> Option<ConstructorView<T>> get(final MethodView<T, ?> method) {
        final ConstructorView<?> constructor = this.bounds.get(method);
        return Option.of((ConstructorView<T>) constructor);
    }
}
