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

package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.web.spec.ParameterPathPartSpec;
import org.dockbox.hartshorn.web.spec.PathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.WildcardPathPartSpec;

/**
 * A utility class for transforming a {@link PathSpec} into a Jetty-compatible path specification
 * string. This is necessary because Jetty uses a different syntax for path specifications, where
 * wildcards are represented by an asterisk ("*") and path parameters are not supported in the same
 * way as in the {@link PathSpec}.
 *
 * <p>This transformer transforms wildcards and parameter parts into Jetty-compatible wildcards.
 * Note that this does not affect internal path parameter extraction, as this is handled in the
 * request handler using the original {@link PathSpec}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyPathSpecTransformer {

    /**
     * Transforms the given {@link PathSpec} into a Jetty-compatible path specification string.
     *
     * @param pathSpec the {@link PathSpec} to transform into a Jetty-compatible path specification
     * string
     *
     * @return a Jetty-compatible path specification string that represents the same path pattern as
     * the given {@link PathSpec}
     */
    public String toJettyPathSpec(PathSpec pathSpec) {
        StringBuilder sb = new StringBuilder();
        for (PathPartSpec part : pathSpec.parts()) {
            sb.append("/");
            switch (part) {
                case WildcardPathPartSpec _, ParameterPathPartSpec _ -> sb.append("*");
                default -> sb.append(part.stringValue());
            }
        }
        return sb.toString();
    }
}
