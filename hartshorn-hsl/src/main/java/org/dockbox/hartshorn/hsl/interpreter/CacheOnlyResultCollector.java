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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CacheOnlyResultCollector implements ResultCollector {

    private final Map<String, Object> results = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;
    private Object globalResult;

    public CacheOnlyResultCollector(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addResult(Object value) {
        this.globalResult = value;
    }

    @Override
    public void addResult(String id, Object value) {
        this.results.put(id, value);
    }

    @Override
    public <T> Option<T> result(Class<T> type) {
        return this.result().filter(type::isInstance).map(type::cast);
    }

    @Override
    public Option<?> result() {
        return Option.of(this.globalResult);
    }

    @Override
    public <T> Option<T> result(String id, Class<T> type) {
        return this.result(id).filter(type::isInstance).map(type::cast);
    }

    @Override
    public Option<?> result(String id) {
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
