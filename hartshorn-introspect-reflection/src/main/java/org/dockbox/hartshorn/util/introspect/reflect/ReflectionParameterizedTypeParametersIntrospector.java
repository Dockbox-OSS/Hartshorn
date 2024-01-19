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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SimpleTypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ReflectionParameterizedTypeParametersIntrospector<T> extends AbstractReflectionTypeParametersIntrospector {

    private final ParameterizedType parameterizedType;
    private TypeParameterList parameters;

    public ReflectionParameterizedTypeParametersIntrospector(TypeView<T> type, ParameterizedType parameterizedType, Introspector introspector) {
        super(type, introspector);
        this.parameterizedType = parameterizedType;
        if (parameterizedType != null && parameterizedType.getRawType() != type.type()) {
            throw new IllegalArgumentException("Type " + type.qualifiedName() + " is not the raw type of " + parameterizedType.getTypeName());
        }
    }

    @Override
    public TypeParameterList allInput() {
        if (this.parameters == null) {
            List<TypeParameterView> typeParameters = new ArrayList<>();
            Type[] actualTypeArguments = this.parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                typeParameters.add(new ReflectionTypeParameterView(actualTypeArgument, this.type(), i, this.introspector()));
            }
            this.parameters = new SimpleTypeParameterList(typeParameters);
        }
        return this.parameters;
    }
}
