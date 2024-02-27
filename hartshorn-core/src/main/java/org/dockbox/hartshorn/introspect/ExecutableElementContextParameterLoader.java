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
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.RuleBasedParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

public class ExecutableElementContextParameterLoader extends RuleBasedParameterLoader<ApplicationBoundParameterLoaderContext> {

    private final ApplicationContext applicationContext;

    public ExecutableElementContextParameterLoader(ApplicationContext applicationContext) {
        super(ApplicationBoundParameterLoaderContext.class);
        this.applicationContext = applicationContext;
        this.add(new ContextParameterLoaderRule(applicationContext));
    }

    @Override
    protected <T> T loadDefault(ParameterView<T> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        ComponentKey<?> componentKey = this.applicationContext.environment().componentKeyResolver().resolve(parameter);

        ComponentRequestContext requestContext = ComponentRequestContext.createForInjectionPoint(new InjectionPoint(parameter));
        Object out = context.provider().get(componentKey, requestContext);

        boolean required = isRequired(parameter);

        if (required && out == null) {
            throw new ComponentRequiredException("Parameter " + parameter.name() + " on " + parameter.declaredBy().qualifiedName() + " is required");
        }
        return parameter.type().cast(out);
    }

    private static boolean isRequired(AnnotatedElementView parameter) {
        return Boolean.TRUE.equals(parameter.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));
    }
}
