/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.web.message;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

/**
 * A {@link WebRequestPathParameters} implementation backed by a {@link Map}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class MapWebRequestPathParameters implements WebRequestPathParameters {

    private final Map<String, String> parameters;

    public MapWebRequestPathParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> asMap() {
        return Map.copyOf(this.parameters);
    }

    @Override
    public Option<String> pathParameter(String name) {
        return Option.of(this.parameters.get(name));
    }

    @Override
    public <T> Option<T> pathParameter(String name, Converter<String, T> converter) {
        return this.pathParameter(name).map(converter::convert);
    }
}
