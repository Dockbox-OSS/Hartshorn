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

package org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.component.populate.inject.AnnotatedInjectionPointRequireRule;
import org.dockbox.hartshorn.component.populate.inject.InjectContextParameterResolver;
import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.component.populate.inject.RequireInjectionPointRule;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ContextParameterLoaderRule implements ParameterLoaderRule<ApplicationBoundParameterLoaderContext> {

    private final RequireInjectionPointRule requireRule = new AnnotatedInjectionPointRequireRule();
    private final InjectContextParameterResolver resolver;

    public ContextParameterLoaderRule(ApplicationContext applicationContext) {
        this.resolver = new InjectContextParameterResolver(applicationContext);
    }

    private PopulateComponentContext<?> createContext(ApplicationBoundParameterLoaderContext context) {
        Object instance = context.instance();
        TypeView<?> type = context.executable().declaredBy();
        return new PopulateComponentContext<>(instance, instance, TypeUtils.adjustWildcards(type, TypeView.class), context.applicationContext());
    }

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        return this.resolver.accepts(new InjectionPoint(parameter));
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        InjectionPoint injectionPoint = new InjectionPoint(parameter);
        Object resolved = this.resolver.resolve(injectionPoint, this.createContext(context));
        if (resolved == null && this.requireRule.isRequired(injectionPoint)) {
            throw new ComponentRequiredException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required");
        }
        return Option.of(parameter.type().cast(resolved));
    }
}
