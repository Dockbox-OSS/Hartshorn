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

import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

import java.util.Map;

/**
 * A Jetty-based implementation of {@link MutableHttpMessageHeaders}, which may reject mutations
 * if the underlying headers are not mutable.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyHttpMessageHeaders implements MutableHttpMessageHeaders {

    private final HttpFields headers;

    public JettyHttpMessageHeaders(HttpFields headers) {
        this.headers = headers;
    }

    @Override
    public Option<String> get(String name) {
        return Option.of(this.headers.get(name));
    }

    @Override
    public Map<String, String> asMap() {
        return this.headers.stream()
                .collect(java.util.stream.Collectors.toMap(
                        HttpField::getName,
                        HttpField::getValue
                ));
    }

    @Override
    public void set(String name, String value) {
        if (this.headers instanceof HttpFields.Mutable mutable) {
            mutable.put(name, value);
            return;
        }
        throw new UnsupportedOperationException("Headers are not mutable");
    }
}
