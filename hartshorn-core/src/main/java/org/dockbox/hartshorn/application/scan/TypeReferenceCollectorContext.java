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

package org.dockbox.hartshorn.application.scan;

import org.dockbox.hartshorn.context.DefaultContext;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TypeReferenceCollectorContext extends DefaultContext {

    private final Set<TypeReferenceCollector> collectors = ConcurrentHashMap.newKeySet();

    public void register(final TypeReferenceCollector collector) {
        this.collectors.add(collector);
    }

    public TypeReferenceCollector collector() {
        return new AggregateTypeReferenceCollector(this.collectors);
    }

    public Set<TypeReferenceCollector> collectors() {
        return Collections.unmodifiableSet(this.collectors);
    }
}