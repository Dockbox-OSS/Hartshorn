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

/**
 * A simple implementation of {@link ContextIdentity}. This implementation does not provide a factory
 * implementation, and will throw an {@link IllegalStateException} when {@link #create()} is invoked.
 *
 * @author Guus Lieben
 *
 * @param <T> The type of context that is identified by this instance.
 *
 * @since 0.5.0
 */
public class SimpleContextIdentity<T extends ContextView> implements ContextIdentity<T> {

    private final Class<T> type;
    private final String name;

    public SimpleContextIdentity(Class<T> type) {
        this(type, null);
    }

    public SimpleContextIdentity(Class<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean requiresApplicationContext() {
        return false;
    }

    @Override
    public T create() {
        throw new IllegalStateException("No fallback defined for context " + this.type.getSimpleName());
    }
}
