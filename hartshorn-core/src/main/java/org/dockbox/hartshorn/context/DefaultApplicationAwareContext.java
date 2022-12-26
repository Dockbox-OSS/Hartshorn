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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.option.Option;

public abstract class DefaultApplicationAwareContext extends DefaultContext implements ApplicationAwareContext {

    private final ApplicationContext applicationContext;

    @SuppressWarnings({ "OverridableMethodCallDuringObjectConstruction", "InstanceofThis" })
    protected DefaultApplicationAwareContext(final ApplicationContext applicationContext) {
        if (applicationContext != null) {
            this.applicationContext = applicationContext;
        }
        else {
            if (this instanceof ApplicationContext self) {
                this.applicationContext = self;
            }
            else if (!this.permitNullableApplicationContext()) {
                throw new IllegalStateException("No application context available");
            }
            else {
                this.applicationContext = null;
            }
        }
    }

    protected boolean permitNullableApplicationContext() {
        return false;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C extends Context> Option<C> first(final ContextKey<C> key) {
        return super.first(key).orCompute(() -> key.create(this.applicationContext));
    }
}
