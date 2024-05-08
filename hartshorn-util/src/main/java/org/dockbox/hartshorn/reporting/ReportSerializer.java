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

/**
 * A serializer for {@link DiagnosticsReport} instances. This is used to serialize a report into a specific format.
 *
 * @param <T> the type of the serialized report
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ReportSerializer<T> {

    /**
     * Serializes the given {@link DiagnosticsReport} into a specific format. The returned value is expected to represent
     * the serialized report. The returned value is not required to be thread-safe.
     *
     * @param report the report to serialize
     * @return the serialized report
     * @throws ReportSerializationException if the serialization fails
     */
    T serialize(DiagnosticsReport report) throws ReportSerializationException;
}
