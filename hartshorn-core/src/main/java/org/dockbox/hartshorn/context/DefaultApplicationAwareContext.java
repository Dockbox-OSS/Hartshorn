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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Simple implementation of {@link ApplicationAwareContext}. This implementation will use the
 * {@link ApplicationContext} that is provided to the constructor, or if none is provided, will
 * attempt to use the current instance as the application context if it is an instance of
 * {@link ApplicationContext}. If neither is available, {@link #permitNullableApplicationContext()}
 * is checked. If {@code null} is not permitted, an {@link IllegalStateException} is thrown.
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public abstract class DefaultApplicationAwareContext extends DefaultProvisionContext implements ApplicationAwareContext {

    private final ApplicationContext applicationContext;

    @SuppressWarnings({ "OverridableMethodCallDuringObjectConstruction", "InstanceofThis" })
    protected DefaultApplicationAwareContext(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            this.applicationContext = applicationContext;
        }
        else if (this instanceof ApplicationContext self) {
            this.applicationContext = self;
        }
        else if (!this.permitNullableApplicationContext()) {
            throw new IllegalStateException("No application context available");
        }
        else {
            this.applicationContext = null;
        }
    }

    /**
     * Returns whether a {@code null} application context is permitted.
     *
     * @return {@code true} if a {@code null} application context is permitted, {@code false}
     */
    protected boolean permitNullableApplicationContext() {
        return false;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key) {
        return super.firstContext(key).orCompute(() -> {
            if (key instanceof ContextKey<C> contextKey) {
                C context = contextKey.create(this.applicationContext);
                this.addContext(context);
                return context;
            }
            return null;
        });
    }
}
