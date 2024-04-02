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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.context.DefaultContext;

/**
 * Carrier context for the {@link DefaultBindingConfigurer}. This context is used to pass the
 * {@link DefaultBindingConfigurer} through the context chain when initializing the application.
 *
 * @see DefaultBindingConfigurer
 * @see ApplicationInitializerContext#initializeInitial()
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class DefaultBindingConfigurerContext extends DefaultContext {

    private DefaultBindingConfigurer configurer = DefaultBindingConfigurer.empty();

    /**
     * Returns the {@link DefaultBindingConfigurer} that is currently configured. This may be empty, if no configurer
     * has been configured yet.
     *
     * @return The {@link DefaultBindingConfigurer} that is currently configured.
     */
    public DefaultBindingConfigurer configurer() {
        return this.configurer;
    }

    /**
     * Composes the given configurer with the current configurer. This is useful for composing multiple configurers
     * together, and thus expanding the default bindings of the application.
     *
     * @param configurer The configurer to compose with the current configurer.
     */
    public void compose(DefaultBindingConfigurer configurer) {
        this.configurer = this.configurer.compose(configurer);
    }

    /**
     * Utility method to access the {@link DefaultBindingConfigurerContext} from the given context. This method will
     * immediately compose the given configurer with the configurer that is currently configured in the context, if it
     * is present. If the context does not contain a {@link DefaultBindingConfigurerContext}, this method will do
     * nothing.
     *
     * @param context The context to access the {@link DefaultBindingConfigurerContext} from.
     * @param configurer The configurer to compose with the configurer that is currently configured in the context.
     */
    public static void compose(ContextView context, DefaultBindingConfigurer configurer) {
        context.firstContext(DefaultBindingConfigurerContext.class)
                .peek(bindingConfigurerContext -> bindingConfigurerContext.compose(configurer));
    }
}
