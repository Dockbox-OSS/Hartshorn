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
import java.util.regex.Pattern;

/**
 * Specification for a path part that represents a static segment of the path. A static path part
 * must match the corresponding segment of the request path exactly for a request to match this
 * specification. For example, the path part "users" will only match a request path segment that is
 * exactly "users". Static path parts are typically used to define fixed segments of a path pattern
 * that do not contain any parameters or variables.
 *
 * <p>Static parts can only match segments that consist of unreserved characters, as defined by
 * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">RFC-3986 Section 2.3</a>.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StaticPathPartSpec implements PathPartSpec {

    private static final Pattern UNRESERVED_CHARACTERS = Pattern.compile("[\\w-_~.]+");
    private final String staticPart;

    public StaticPathPartSpec(String staticPart) {
        this.staticPart = staticPart;
    }

    /**
     * Parses the given path part as a static path part specification. A valid static path part must
     * consist of one or more unreserved characters, as defined by
     * <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">RFC-3986 Section 2.3</a>.
     * If the given part is valid, an {@link Option} containing a new {@link StaticPathPartSpec}
     * instance will be returned.
     *
     * @param part the path part to parse as a static path part specification
     *
     * @return an {@link Option} containing a new {@link StaticPathPartSpec} instance if the given
     * part is valid, or otherwise an empty {@link Option}
     */
    public static Option<StaticPathPartSpec> parse(String part) {
        if (part.isEmpty() || !UNRESERVED_CHARACTERS.matcher(part).matches()) {
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
