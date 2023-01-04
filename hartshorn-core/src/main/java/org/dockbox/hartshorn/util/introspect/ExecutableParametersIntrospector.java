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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ExecutableParametersIntrospector {

    List<TypeView<?>> types();

    List<TypeView<?>> genericTypes();

    List<ParameterView<?>> all();

    List<ParameterView<?>> annotedWith(Class<? extends Annotation> annotation);

    Option<ParameterView<?>> at(int index);

    int count();

    Object[] loadFromContext(final Scope scope);

    Object[] loadFromContext();

    boolean matches(Class<?>... parameterTypes);

    boolean matches(List<Class<?>> parameterTypes);

}
