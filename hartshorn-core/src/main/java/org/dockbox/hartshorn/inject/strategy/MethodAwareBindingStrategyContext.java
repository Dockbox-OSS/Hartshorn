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

package org.dockbox.hartshorn.inject.strategy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.inject.DependencyDeclarationContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class MethodAwareBindingStrategyContext<T> extends DefaultApplicationAwareContext implements BindingStrategyContext<T> {

    private final DependencyDeclarationContext<T> componentContainer;
    private final MethodView<T, ?> method;

    public MethodAwareBindingStrategyContext(ApplicationContext applicationContext, DependencyDeclarationContext<T> componentContainer, MethodView<T, ?> method) {
        super(applicationContext);
        this.componentContainer = componentContainer;
        this.method = method;
    }

    @Override
    public DependencyDeclarationContext<T> declarationContext() {
        return this.componentContainer;
    }
    
    public MethodView<T, ?> method() {
        return this.method;
    }
}
