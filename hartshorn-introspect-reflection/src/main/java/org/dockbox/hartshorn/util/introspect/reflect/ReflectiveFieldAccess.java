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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a functional interface that can be used to read a value from a field. This is used to
 * abstract the process of getting a value from a field, and is typically used by {@link
 * org.dockbox.hartshorn.util.introspect.view.FieldView#get(Object)} to get a value from a field.
 *
 * @param <T> the type of the field
 * @param <P> the type of the instance
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ReflectiveFieldAccess<T, P> {

    /**
     * Gets the value of the field on the given instance. If the field could not be read from, an
     * exception is thrown.
     *
     * @param instance the instance from which to get the value
     * @return the value of the field
     * @throws Throwable if the field could not be read from
     */
    Option<T> get(P instance) throws Throwable;
}
