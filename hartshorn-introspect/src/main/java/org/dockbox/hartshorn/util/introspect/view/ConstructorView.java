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

import org.dockbox.hartshorn.util.option.Attempt;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

public interface ConstructorView<T> extends ExecutableElementView<T, T> {

    Constructor<T> constructor();

    default Attempt<T, Throwable> create(final Object... arguments) {
        return this.create(Arrays.asList(arguments));
    }

    Attempt<T, Throwable> create(Collection<?> arguments);

    TypeView<T> type();

    String name();

    String qualifiedName();

}
