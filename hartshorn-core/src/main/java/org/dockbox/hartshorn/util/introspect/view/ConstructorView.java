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

import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.util.option.Attempt;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public interface ConstructorView<T> extends ExecutableElementView<T> {

    Constructor<T> constructor();

    default Attempt<T, Throwable> create(final Object... arguments) {
        return this.create(List.of(arguments));
    }

    Attempt<T, Throwable> create(Collection<?> arguments);

    Attempt<T, Throwable> createWithContext(Scope scope);

    Attempt<T, Throwable> createWithContext();

    TypeView<T> type();

    String name();

    String qualifiedName();

}
