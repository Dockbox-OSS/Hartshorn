/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

import java.util.List;

import jakarta.annotation.PostConstruct;

public class ComponentPostConstructorImpl implements ComponentPostConstructor {

    private final ApplicationContext applicationContext;

    public ComponentPostConstructorImpl(final InitializingContext context) {
        this.applicationContext = context.applicationContext();
    }

    @Override
    public <T> T doPostConstruct(final T type) throws ApplicationException {
        final TypeView<T> typeView = this.applicationContext.environment().introspect(type);
        final List<MethodView<T, ?>> postConstructMethods = typeView.methods().annotatedWith(PostConstruct.class);

        for (final MethodView<T, ?> postConstructMethod : postConstructMethods) {
            final Attempt<?, Throwable> result = postConstructMethod.invokeWithContext(type);

            if (result.errorPresent()) {
                final Throwable error = result.error();

                if (error instanceof ApplicationException applicationException) {
                    throw applicationException;
                } else {
                    throw new ApplicationException(error);
                }
            }
        }
        return type;
    }
}
