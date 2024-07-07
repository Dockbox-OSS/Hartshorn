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

package org.dockbox.hartshorn.inject;

import java.util.List;

import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.context.SimpleContextIdentity;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A default implementation of {@link ProvisionContext}. This implementation overrides the
 * default behavior of {@link DefaultContext} to use {@link ContextKey}s instead of
 * {@link SimpleContextIdentity simple identities}.
 *
 * @see DefaultContext
 * @see ProvisionContext
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public abstract class DefaultProvisionContext extends DefaultContext implements ProvisionContext {

    @Override
    public <C extends ContextView> Option<C> firstContext(Class<C> context) {
        return ProvisionContext.super.firstContext(context);
    }

    @Override
    public <C extends ContextView> List<C> contexts(Class<C> context) {
        return ProvisionContext.super.contexts(context);
    }
}
