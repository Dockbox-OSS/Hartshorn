/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public interface TypeMethodsIntrospector<T> {

    default Result<MethodView<T, ?>> named(final String name) {
        return this.named(name, List.of());
    }

    default Result<MethodView<T, ?>> named(final String name, final Class<?>... parameterTypes) {
        return this.named(name, List.of(parameterTypes));
    }

    Result<MethodView<T, ?>> named(String name, Collection<Class<?>> parameterTypes);

    List<MethodView<T, ?>> all();

    List<MethodView<T, ?>> declared();

    List<MethodView<T, ?>> annotatedWith(Class<? extends Annotation> annotation);

    List<MethodView<T, ?>> bridges();
}
