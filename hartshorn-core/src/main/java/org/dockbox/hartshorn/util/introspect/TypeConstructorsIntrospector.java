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

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.inject.Inject;

public interface TypeConstructorsIntrospector<T> {

    Result<ConstructorView<T>> defaultConstructor();

    List<ConstructorView<T>> annotatedWith(Class<? extends Annotation> annotation);

    default List<ConstructorView<T>> injectable() {
        return this.annotatedWith(Inject.class);
    }

    default List<ConstructorView<T>> bound() {
        return this.annotatedWith(Bound.class);
    }

    default Result<ConstructorView<T>> withParameters(final Class<?>... parameters) {
        return this.withParameters(List.of(parameters));
    }

    Result<ConstructorView<T>> withParameters(List<Class<?>> parameters);

    List<ConstructorView<T>> all();

    int count();

}
