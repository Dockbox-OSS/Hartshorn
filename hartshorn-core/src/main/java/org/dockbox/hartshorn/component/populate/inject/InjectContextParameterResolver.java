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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.populate.PopulateComponentContext;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.inject.Context;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class InjectContextParameterResolver implements InjectParameterResolver {

    @Override
    public boolean accepts(InjectionPoint injectionPoint) {
        return injectionPoint.injectionPoint().annotations().has(Context.class) && injectionPoint.type().isChildOf(org.dockbox.hartshorn.context.Context.class);
    }

    @Override
    public Object resolve(InjectionPoint injectionPoint, PopulateComponentContext<?> context) {
        ApplicationContext applicationContext = context.applicationContext();
        String name = injectionPoint.injectionPoint().annotations().get(Context.class).map(Context::value).orNull();

        TypeView<? extends org.dockbox.hartshorn.context.Context> type = TypeUtils.adjustWildcards(injectionPoint.type(), TypeView.class);
        ContextKey<? extends org.dockbox.hartshorn.context.Context> key = ContextKey.of(type.type());
        if (StringUtilities.notEmpty(name)) {
            key = key.mutable().name(name).build();
        }

        return applicationContext.first(key).orNull();
    }
}
