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

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheOnlyResultCollector implements ResultCollector {

    private Object globalResult;
    private final Map<String, Object> results = new ConcurrentHashMap<>();

    @Override
    public void addResult(final Object value) {
        this.globalResult = value;
    }

    @Override
    public void addResult(final String id, final Object value) {
        this.results.put(id, value);
    }

    @Override
    public <T> Option<T> result() {
        return Option.of((T) this.globalResult);
    }

    @Override
    public <T> Option<T> result(final String id) {
        return Option.of((T) this.results.get(id));
    }

    @Override
    public void clear() {
        this.results.clear();
    }
}
