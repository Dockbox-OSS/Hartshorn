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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.option.Attempt;

public class ComponentFieldInjectionPoint<T> implements ComponentInjectionPoint<T> {

    private final FieldView<T, ?> field;

    public ComponentFieldInjectionPoint(FieldView<T, ?> field) {
        this.field = field;
    }

    @Override
    public void processObjects(PopulateComponentContext<T> context, List<Object> objectsToInject) {
        if (objectsToInject.size() == 1) {
            Object objectToInject = objectsToInject.get(0);
            T instance = context.instance();
            if (objectToInject instanceof Collection<?> collection) {
                Attempt<?, Throwable> previousValue = this.field.get(instance);
                if (previousValue.present() && previousValue.get() instanceof Collection<?> existingCollection) {
                    existingCollection.addAll(TypeUtils.adjustWildcards(collection, Collection.class));
                    return;
                }
            }
            this.field.set(instance, objectToInject);
        }
        else {
            throw new UnsupportedOperationException("Cannot inject multiple objects into a single field");
        }
    }

    @Override
    public ElementAnnotationsIntrospector annotations() {
        return this.field.annotations();
    }

    @Override
    public Set<InjectionPoint> injectionPoints() {
        return Set.of(new InjectionPoint(this.field.genericType(), this.field));
    }

    @Override
    public String qualifiedName() {
        return this.field.name();
    }
}
