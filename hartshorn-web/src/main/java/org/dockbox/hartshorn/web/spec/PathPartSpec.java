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

import org.dockbox.hartshorn.web.spec.parser.PathPartSpecParser;

import java.util.Map;

/**
 * Specification for a single part of a path pattern. This is used by the {@link PathSpec} to match
 * individual parts of a request path, and can be implemented to support custom path part
 * specifications.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface PathPartSpec {

    /**
     * Returns a string representation of this path part specification, which can be used for
     * debugging purposes or for reconstructing the original path pattern. This should ideally
     * return a string that, when parsed by the {@link PathPartSpecParser}, would produce an
     * equivalent {@link PathPartSpec} instance.
     *
     * @return a string representation of this path part specification
     */
    String stringValue();

    /**
     * Determines whether the given path part matches this path part specification. If the match is
     * successful, any path parameters extracted from the path part should be added to the provided
     * map of path parameters.
     *
     * @param pathPart the path part to match against this specification
     * @param pathParameters a map to which any path parameters extracted from the path part should
     * be added if the match is successful
     *
     * @return {@code true} if the given path part matches this specification, or {@code false}
     * otherwise
     */
    boolean matches(String pathPart, Map<String, String> pathParameters);
}
