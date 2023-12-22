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

package org.dockbox.hartshorn.util.introspect.view;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a view of a constructor. This view can be used to invoke the constructor and to
 * retrieve information about the constructor.
 *
 * @param <T> the type of the class that declares the constructor
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
public interface ConstructorView<T> extends ExecutableElementView<T> {

    /**
     * Returns the constructor represented by this view.
     *
     * @return the constructor, if available
     */
    Option<Constructor<T>> constructor();

    /**
     * Creates a new instance of the class that declares the constructor represented by this view.
     * The provided arguments are passed to the constructor. If the constructor is not accessible,
     * it will be made accessible. If the constructor is not available, an empty {@link Option} is
     * returned. If the constructor throws an exception, the exception is re-thrown.
     *
     * @param arguments the arguments to pass to the constructor
     * @return a new instance of the class that declares the constructor
     */
    default Option<T> create(Object... arguments) throws Throwable {
        return this.create(Arrays.asList(arguments));
    }

    /**
     * Creates a new instance of the class that declares the constructor represented by this view.
     * The provided arguments are passed to the constructor. If the constructor is not accessible,
     * it will be made accessible. If the constructor is not available, an empty {@link Option} is
     * returned. If the constructor throws an exception, the exception is re-thrown.
     *
     * @param arguments the arguments to pass to the constructor
     * @return a new instance of the class that declares the constructor
     */
    Option<T> create(Collection<?> arguments) throws Throwable;

    /**
     * Returns the type of the class that declares the constructor represented by this view.
     *
     * @return the type of the class that declares the constructor
     */
    TypeView<T> type();

    @Override
    default TypeView<?> resultType() {
        return this.type();
    }
}
