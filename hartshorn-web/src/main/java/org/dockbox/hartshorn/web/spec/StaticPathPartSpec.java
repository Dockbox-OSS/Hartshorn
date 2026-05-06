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

package org.dockbox.hartshorn.web.spec;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;

public class StaticPathPartSpec implements PathPartSpec {

    private final String staticPart;

    public StaticPathPartSpec(String staticPart) {
        this.staticPart = staticPart;
    }

    public static Option<StaticPathPartSpec> parse(String part) {
        if (part.isEmpty() || !part.matches("[\\w-_]+")) {
            return Option.empty();
        }
        return Option.of(new StaticPathPartSpec(part));
    }

    @Override
    public String stringValue() {
        return this.staticPart;
    }

    @Override
    public boolean matches(String pathPart, Map<String, String> pathParameters) {
        return this.staticPart.equals(pathPart);
    }
}
