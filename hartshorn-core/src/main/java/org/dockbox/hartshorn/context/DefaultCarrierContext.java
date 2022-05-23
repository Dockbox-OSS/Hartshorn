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
import org.dockbox.hartshorn.util.Result;

/**
 * The default implementation of {@link CarrierContext}. This implementation stores the active
 * {@link ApplicationContext} directly as a field.
 */
public class DefaultCarrierContext extends DefaultContext implements CarrierContext {

    private final ApplicationContext applicationContext;

    public DefaultCarrierContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C extends Context> Result<C> first(final Class<C> context) {
        return super.first(this.applicationContext(), context);
    }
}
