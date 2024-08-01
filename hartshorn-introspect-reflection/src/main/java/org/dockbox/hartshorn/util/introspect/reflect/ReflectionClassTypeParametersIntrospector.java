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

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.SimpleTypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionTypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: #1059 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ReflectionClassTypeParametersIntrospector extends AbstractReflectionTypeParametersIntrospector {

    private TypeParameterList inputParameters;

    public ReflectionClassTypeParametersIntrospector(TypeView<?> type, Introspector introspector) {
        super(type, introspector);
    }

    @Override
    public TypeParameterList allInput() {
        if (this.inputParameters == null) {
            List<TypeParameterView> parameters = new ArrayList<>();
            TypeVariable<? extends Class<?>>[] typeParameters = this.type().type().getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                TypeVariable<?> typeParameter = typeParameters[i];
                parameters.add(new ReflectionTypeParameterView(typeParameter, this.type(), i, this.introspector()));
            }
            this.inputParameters = new SimpleTypeParameterList(parameters);
        }
        return this.inputParameters;
    }
}
