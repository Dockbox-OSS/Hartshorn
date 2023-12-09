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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.inject.Populate;
import org.dockbox.hartshorn.inject.Populate.Type;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ComponentInjectionPointsResolver} that resolves the injection points of a component by
 * inspecting the methods and fields of the component. The injection points are resolved based on
 * the {@link Populate} annotation on the component type. If no {@link Populate} annotation is
 * present, all injection points will be resolved.
 *
 * @see Populate
 * @see ComponentInjectionPoint
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class MethodsAndFieldsInjectionPointResolver implements ComponentInjectionPointsResolver {

    @Override
    public <T> Set<ComponentInjectionPoint<T>> resolve(TypeView<T> type) {
        List<Type> types = type.annotations().get(Populate.class)
                .map(Populate::value)
                .map(List::of)
                .orElseGet(() -> List.of(Type.values()));

        Set<ComponentInjectionPoint<T>> injectionPoints = new HashSet<>();
        if (types.contains(Type.EXECUTABLES)) {
            type.methods().all().stream()
                    .map(ComponentMethodInjectionPoint::new)
                    .forEach(injectionPoints::add);
        }
        if (types.contains(Type.FIELDS)) {
            type.fields().all().stream()
                    .map(ComponentFieldInjectionPoint::new)
                    .forEach(injectionPoints::add);
        }
        return injectionPoints;
    }
}
