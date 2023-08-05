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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionClassTypeParametersIntrospector extends AbstractReflectionTypeParametersIntrospector {

    private List<TypeParameterView> inputParameters;

    public ReflectionClassTypeParametersIntrospector(final TypeView<?> type, final Introspector introspector) {
        super(type, introspector);
    }

    @Override
    @Deprecated(since = "23.1", forRemoval = true)
    public List<TypeView<?>> from(final Class<?> fromInterface) {
        return List.of();
    }

    @Override
    public List<TypeParameterView> allInput() {
        if (this.inputParameters == null) {
            this.inputParameters = Arrays.stream(this.type().type().getTypeParameters())
                    .map(parameter -> new ReflectionTypeParameterView(parameter, this.type(), this.introspector()))
                    .collect(Collectors.toList());
        }
        return this.inputParameters;
    }
}
