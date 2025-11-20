/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;

/**
 * Immutable wrapper for {@link Context contexts}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ImmutableContextView implements ContextView {

    private final Context context;

    public ImmutableContextView(Context context) {
        this.context = context;
    }

    @Override
    public List<ContextView> contexts() {
        return this.context.contexts();
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(Class<C> context) {
        return this.context.firstContext(context);
    }

    @Override
    public <C extends ContextView> List<C> contexts(Class<C> context) {
        return this.context.contexts(context);
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key) {
        return this.context.firstContext(key);
    }

    @Override
    public <C extends ContextView> List<C> contexts(ContextIdentity<C> key) {
        return this.context.contexts(key);
    }

    @Override
    public void copyToContext(Context context) {
        this.context.copyToContext(context);
    }

    @Override
    public String toString() {
        if (this.context instanceof DefaultContext defaultContext) {
            MultiMap<String, ContextView> namedContexts = defaultContext.namedContexts();
            Set<ContextView> unnamedContexts = defaultContext.unnamedContexts();
            return ObjectDescriber.of(this)
                .field("namedContexts", namedContexts)
                .field("unnamedContexts", unnamedContexts)
                .describe();
        }
        else {
            return ObjectDescriber.of(this)
                .field("contexts", this.contexts())
                .describe();
        }
    }
}
