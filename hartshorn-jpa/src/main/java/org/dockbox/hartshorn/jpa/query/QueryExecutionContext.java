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

package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;

public class QueryExecutionContext extends DefaultProvisionContext {

    private final Map<MethodView<?, ?>, LockModeType> lockModes = new ConcurrentHashMap<>();
    private final Map<MethodView<?, ?>, FlushModeType> flushModes = new ConcurrentHashMap<>();

    public LockModeType lockMode(final MethodView<?, ?> method) {
        return this.lockModes.get(method);
    }

    public FlushModeType flushMode(final MethodView<?, ?> method) {
        return this.flushModes.get(method);
    }

    public void lockMode(final MethodView<?, ?> method, final LockModeType lockMode) {
        this.lockModes.put(method, lockMode);
    }

    public void flushMode(final MethodView<?, ?> method, final FlushModeType flushMode) {
        this.flushModes.put(method, flushMode);
    }

    public boolean hasLockMode(final MethodView<?, ?> method) {
        return this.lockModes.containsKey(method);
    }

    public boolean hasFlushMode(final MethodView<?, ?> method) {
        return this.flushModes.containsKey(method);
    }
}
