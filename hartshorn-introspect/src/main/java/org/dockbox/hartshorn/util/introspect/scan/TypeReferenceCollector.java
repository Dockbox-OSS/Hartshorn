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

package org.dockbox.hartshorn.util.introspect.scan;

import org.dockbox.hartshorn.reporting.Reportable;

import java.util.Set;

/**
 * A {@link TypeReferenceCollector} is responsible for collecting {@link TypeReference}s. The result of a collection
 * is a {@link Set} of {@link TypeReference}s. Type collecting is often used by application environments to discover
 * types that are available on the classpath.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface TypeReferenceCollector extends Reportable {

    /**
     * Collects {@link TypeReference}s.
     *
     * @return A {@link Set} of {@link TypeReference}s
     * @throws TypeCollectionException When an error occurs while collecting {@link TypeReference}s.
     */
    Set<TypeReference> collect() throws TypeCollectionException;
}
