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

/**
 * Resolves the {@link ComponentKey} for a given {@link InjectionPoint}. This supports customization
 * of the key's name and enablement requirements through {@link InjectionPointNameResolver} and
 * {@link EnableInjectionPointRule} respectively.
 *
 * <p>If the injection point is a {@link Collection},  the element type is used as the type and the key
 * will be configured for collecting a {@link org.dockbox.hartshorn.inject.binding.collection.ComponentCollection}.
 *
 * @see InjectionPointNameResolver
 * @see EnableInjectionPointRule
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
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

    /**
     * Builds a {@link ComponentKey} for the given {@link InjectionPoint}.
     *
     * @param injectionPoint the injection point to build the key for
     * @return the component key
     */
    public ComponentKey<?> buildComponentKey(InjectionPoint injectionPoint) {
        ComponentKey.Builder<?> componentKey = ComponentKey.builder(injectionPoint.type())
                .name(resolveName(injectionPoint))
                .enable(shouldEnable(injectionPoint));
        return customizeComponentKey(componentKey, injectionPoint);
    }

    /**
     * Customizes the given {@link ComponentKey.Builder} for the given {@link InjectionPoint}. By
     * default this method will only customize the key for {@link Collection} types, by setting the
     * element type and enabling collection mode.
     *
     * @param keyBuilder the key builder to customize
     * @param injectionPoint the injection point to customize the key for
     * @return the customized key builder
     */
    protected ComponentKey<?> customizeComponentKey(ComponentKey.Builder<?> keyBuilder, InjectionPoint injectionPoint) {
        if (injectionPoint.type().isChildOf(Collection.class)) {
            TypeView<?> elementType = resolveCollectionElementType(injectionPoint);
            return keyBuilder.type(elementType).collector().build();
        }
        else {
            return keyBuilder.build();
        }
    }

    /**
     * Resolves the element type of the given {@link Collection} injection point. If the element type
     * cannot be resolved, an exception is thrown.
     *
     * @param injectionPoint the injection point to resolve the element type for
     * @return the element type
     *
     * @throws ComponentPopulateException if the element type cannot be resolved
     */
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

    /**
     * Resolves the name for the given {@link InjectionPoint}. This will use the first non-null and
     * non-blank name returned by the {@link InjectionPointNameResolver}s configured in this resolver.
     *
     * @param injectionPoint the injection point to resolve the name for
     * @return the name
     */
    private String resolveName(InjectionPoint injectionPoint) {
        return this.nameResolvers.stream()
                .map(resolver -> resolver.resolve(injectionPoint))
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(null);
    }

    /**
     * Determines whether the given {@link InjectionPoint} should be enabled. This will return true
     * if all {@link EnableInjectionPointRule}s configured in this resolver return true.
     *
     * @param injectionPoint the injection point to determine whether it should be enabled
     * @return true if the injection point should be enabled
     */
    private boolean shouldEnable(InjectionPoint injectionPoint) {
        return this.enableComponentRules.isEmpty() || this.enableComponentRules.stream().allMatch(rule -> rule.shouldEnable(injectionPoint));
    }
}
