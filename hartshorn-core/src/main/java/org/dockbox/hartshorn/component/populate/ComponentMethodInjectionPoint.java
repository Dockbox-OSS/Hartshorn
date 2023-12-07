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

package org.dockbox.hartshorn.component.populate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public class ComponentMethodInjectionPoint<T> implements ComponentInjectionPoint<T> {

    private final MethodView<T, ?> method;

    public ComponentMethodInjectionPoint(MethodView<T, ?> method) {
        this.method = method;
    }

    @Override
    public void processObjects(PopulateComponentContext<T> context, List<Object> objectsToInject) {
        this.method.invoke(context.instance(), objectsToInject.toArray());
    }

    @Override
    public ElementAnnotationsIntrospector annotations() {
        return this.method.annotations();
    }

    @Override
    public Set<InjectionPoint> injectionPoints() {
        return this.method.parameters().all().stream()
                .map(parameter -> new InjectionPoint(parameter.genericType(), parameter))
                .collect(Collectors.toSet());
    }

    @Override
    public String qualifiedName() {
        return this.method.qualifiedName();
    }
}
