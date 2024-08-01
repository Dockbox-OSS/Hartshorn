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

package org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.util.Node;

/**
 * A diagnostics report which uses a {@link Node} as its root node. A report may be used to collect
 * diagnostics information, and serialize it to a specific format. The report itself does not
 * provide any serialization functionality, but does provide a method to serialize itself using a
 * {@link ReportSerializer}.
 *
 * @see ReportSerializer
 * @see DiagnosticsPropertyCollector
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface DiagnosticsReport {

    /**
     * Returns the root node of this report. The root node is the entry point for all information
     * that is collected by this report.
     *
     * @return the root node of this report
     */
    Node<?> root();

    /**
     * Serializes this report using the given {@link ReportSerializer}. The serializer is expected
     * to return a value that represents the serialized report.
     *
     * @param serializer the serializer to use
     * @param <T> the type of the serialized report
     *
     * @return the serialized report
     *
     * @throws ReportSerializationException if the serialization fails
     */
    default <T> T serialize(ReportSerializer<T> serializer) throws ReportSerializationException {
        return serializer.serialize(this);
    }
}
