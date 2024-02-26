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

import org.dockbox.hartshorn.component.populate.inject.InjectionPoint;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

public class InjectionPointParameterLoaderRule implements ParameterLoaderRule<ApplicationBoundParameterLoaderContext> {

    private final ComponentRequestContext requestContext;

    public InjectionPointParameterLoaderRule(ComponentRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        return requestContext.isForInjectionPoint() && parameter.type().isChildOf(InjectionPoint.class);
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, ApplicationBoundParameterLoaderContext context, Object... args) {
        InjectionPoint injectionPoint = requestContext.injectionPoint();
        return Option.of(parameter.type().cast(injectionPoint));
    }
}
