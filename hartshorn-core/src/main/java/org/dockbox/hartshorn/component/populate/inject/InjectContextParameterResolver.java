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

package org.dockbox.hartshorn.component.populate.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.inject.Named;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link InjectParameterResolver} implementation that resolves {@link ContextView} instances. This resolver
 * will only accept injection points that implement {@link ContextView}. If multiple contexts are available,
 * the first one is returned.
 *
 * @see ContextView
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class InjectContextParameterResolver implements InjectParameterResolver {

    private final ApplicationContext applicationContext;

    public InjectContextParameterResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean accepts(InjectionPoint injectionPoint) {
        TypeView<?> type = injectionPoint.type();
        boolean childOf = type.isChildOf(ContextView.class);
        if (!childOf) {
            return false;
        }

        ContextIdentity<? extends ContextView> contextKey = getContextKey(injectionPoint);
        // If absent, we still prefer bindings from the application context in case
        // manual bindings exist. A common example of this is the ApplicationContext
        // itself, which is a self-containing context.
        return this.applicationContext.first(contextKey).present();
    }

    @Override
    public Object resolve(InjectionPoint injectionPoint, PopulateComponentContext<?> context) {
        return this.applicationContext.first(key).orNull();
        ContextIdentity<? extends ContextView> key = getContextKey(injectionPoint);
    }

    private static ContextKey<? extends ContextView> getContextKey(InjectionPoint injectionPoint) {
        String name = injectionPoint.injectionPoint().annotations()
                // Unlike components, contexts only support named qualifiers, so we don't
                // need to include MetaQualifier annotated annotations here.
                .get(Named.class)
                .map(Named::value)
                .orNull();

        TypeView<? extends ContextView> type = TypeUtils.adjustWildcards(injectionPoint.type(), TypeView.class);
        ContextKey<? extends ContextView> key = ContextKey.of(type.type());
        if (StringUtilities.notEmpty(name)) {
            key = key.mutable().name(name).build();
        }
        return key;
    }
}
