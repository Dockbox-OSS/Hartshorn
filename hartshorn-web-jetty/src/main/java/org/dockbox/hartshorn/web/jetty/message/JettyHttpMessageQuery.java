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

package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.HttpMessageQuery;
import org.eclipse.jetty.util.Fields;

import java.util.List;

/**
 * An implementation of {@link HttpMessageQuery} for Jetty's {@link Fields}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyHttpMessageQuery implements HttpMessageQuery {
    private final Fields queryParameters;

    public JettyHttpMessageQuery(Fields queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public Option<String> get(String name) {
        List<String> values = getAll(name);
        if (values.isEmpty()) {
            return Option.empty();
        }
        else {
            return Option.of(String.join(",", values));
        }
    }

    @Override
    public List<String> getAll(String name) {
        return List.copyOf(this.queryParameters.getValues(name));
    }

    @Override
    public MultiMap<String, String> asMultiMap() {
        MultiMap<String, String> entries = new ArrayListMultiMap<>();
        this.queryParameters.toMultiMap().forEach(entries::putAll);
        return entries;
    }

    @Override
    public boolean notEmpty() {
        return !this.queryParameters.isEmpty();
    }

    @Override
    public String asString() {
        StringBuilder queryString = new StringBuilder();
        this.queryParameters.forEach(field -> {
            if (!queryString.isEmpty()) {
                queryString.append("&");
            }
            queryString.append(field.getName())
                    .append("=")
                    .append(String.join(",", field.getValues()));
        });
        return queryString.toString();
    }
}
