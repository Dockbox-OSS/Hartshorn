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

package org.dockbox.hartshorn.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultProvisionContext;

public class ConfigurationURIContextList extends DefaultProvisionContext {

    public static final ContextIdentity<ConfigurationURIContextList> CONTEXT_KEY = ContextKey.builder(ConfigurationURIContextList.class)
            .fallback(ConfigurationURIContextList::new)
            .build();

    private final Set<ConfigurationURIContext> contexts = ConcurrentHashMap.newKeySet();

    public Set<ConfigurationURIContext> uris() {
        return this.contexts;
    }

    public void add(Set<ConfigurationURIContext> contexts) {
        this.contexts.addAll(contexts);
    }

    public void add(ConfigurationURIContext context) {
        this.contexts.add(context);
    }
}
