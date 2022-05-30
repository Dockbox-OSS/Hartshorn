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

package org.dockbox.hartshorn.events.parents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.events.EventBus;

public abstract class ContextCarrierEvent extends DefaultApplicationAwareContext implements Event {

    private ApplicationContext context;

    public ContextCarrierEvent() {
        super(null);
    }

    @Override
    public ApplicationContext applicationContext() {
        if (this.context == null) {
            throw new IllegalStateException("Event instance has not been enhanced with application context");
        }
        return this.context;
    }

    @Override
    public <C extends Context> void add(final C context) {
        if (context instanceof ApplicationContext applicationContext) {
            this.with(applicationContext);
        }
        super.add(context);
    }

    @Override
    public ContextCarrierEvent with(final ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Cannot enhance with non-existent application context");
        }
        this.context = context;
        return this;
    }

    @Override
    public @NonNull Event post() {
        this.applicationContext().get(EventBus.class).post(this);
        return this;
    }
}
