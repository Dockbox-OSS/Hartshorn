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

package org.dockbox.hartshorn.component.populate.inject;

import java.util.Collection;
import java.util.Set;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentPopulateException;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class ComponentKeyResolver {

    private final Set<InjectionPointNameResolver> nameResolvers;
    private final Set<EnableInjectionPointRule> enableComponentRules;

    public ComponentKeyResolver(
            Set<InjectionPointNameResolver> nameResolvers,
            Set<EnableInjectionPointRule> enableComponentRules
    ) {
        this.nameResolvers = nameResolvers;
        this.enableComponentRules = enableComponentRules;
    }

    public ComponentKey<?> buildComponentKey(InjectionPoint injectionPoint) {
        ComponentKey.Builder<?> componentKey = ComponentKey.builder(injectionPoint.type())
                .name(resolveName(injectionPoint))
                .enable(shouldEnable(injectionPoint));
        return customizeComponentKey(componentKey, injectionPoint);
    }

    protected ComponentKey<?> customizeComponentKey(ComponentKey.Builder<?> keyBuilder, InjectionPoint injectionPoint) {
        if (injectionPoint.type().isChildOf(Collection.class)) {
            TypeView<?> elementType = resolveCollectionElementType(injectionPoint);
            return keyBuilder.type(elementType).collector().build();
        }
        else {
            return keyBuilder.build();
        }
    }

    protected TypeView<?> resolveCollectionElementType(InjectionPoint injectionPoint) {
        Option<TypeView<?>> elementType = injectionPoint.type()
                .typeParameters()
                .resolveInputFor(Collection.class)
                .atIndex(0)
                .flatMap(TypeParameterView::resolvedType);
        if (elementType.absent()) {
            throw new ComponentPopulateException("Failed to populate injection point " + injectionPoint.injectionPoint().qualifiedName() + ", could not resolve collection element type", null);
        }
        return elementType.get();
    }

    private String resolveName(InjectionPoint injectionPoint) {
        return this.nameResolvers.stream()
                .map(resolver -> resolver.resolve(injectionPoint))
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(null);
    }

    private boolean shouldEnable(InjectionPoint injectionPoint) {
        return this.enableComponentRules.stream()
                .map(rule -> rule.shouldEnable(injectionPoint))
                .reduce(true, Boolean::logicalAnd);
    }
}
