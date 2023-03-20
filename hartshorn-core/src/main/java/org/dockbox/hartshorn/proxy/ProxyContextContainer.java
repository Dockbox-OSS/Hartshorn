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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.util.collections.MultiMap;

import java.util.Set;

public class ProxyContextContainer extends DefaultProvisionContext {

    private final Runnable onModify;

    public ProxyContextContainer(final Runnable onModify) {
        this.onModify = onModify;
    }

    public Set<Context> contexts() {
        return super.unnamedContexts();
    }

    // Change access level to public
    public MultiMap<String, Context> namedContexts() {
        return super.namedContexts();
    }

    @Override
    public <C extends Context> void add(final C context) {
        super.add(context);
        this.onModify.run();
    }

    @Override
    public <C extends Context> void add(final String name, final C context) {
        super.add(name, context);
        this.onModify.run();
    }
}
