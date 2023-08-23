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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheOnlyResultCollector implements ResultCollector {

    private final Map<String, Object> results = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    private Object globalResult;

    public CacheOnlyResultCollector(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addResult(final Object value) {
        this.globalResult = value;
    }

    @Override
    public void addResult(final String id, final Object value) {
        this.results.put(id, value);
    }

    @Override
    public <T> Option<T> result(final Class<T> type) {
        return this.result().filter(type::isInstance).map(type::cast);
    }

    @Override
    public Option<?> result() {
        return Option.of(this.globalResult);
    }

    @Override
    public <T> Option<T> result(final String id, final Class<T> type) {
        return this.result(id).filter(type::isInstance).map(type::cast);
    }

    @Override
    public Option<?> result(final String id) {
        return Option.of(this.results.get(id));
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
