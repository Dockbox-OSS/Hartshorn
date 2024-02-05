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

/**
 * Represents a functional interface that can be used to write a value to a field. This is used to
 * abstract the process of setting a value on a field, and is typically used by {@link
 * org.dockbox.hartshorn.util.introspect.view.FieldView#set(Object, Object)} to set a value on a field.
 *
 * @param <T> the type of the field
 * @param <P> the type of the instance
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ReflectiveFieldWriter<T, P> {

    /**
     * Sets the value of the field on the given instance to the given value. If the field could not
     * be written to, an exception is thrown.
     *
     * @param instance the instance on which to set the value
     * @param value the value to set
     * @throws Throwable if the field could not be written to
     */
    void set(P instance, T value) throws Throwable;
}
