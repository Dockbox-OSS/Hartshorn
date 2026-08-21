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

package org.dockbox.hartshorn.reporting.serialize;

import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.ReportSerializer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * A {@link ReportSerializer} which uses Jackson's {@link ObjectMapper} to serialize a
 * {@link DiagnosticsReport} to a specific format.
 *
 * @see DiagnosticsReport
 * @see ReportSerializer
 * @see ObjectMapper
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ObjectMapperReportSerializer implements ReportSerializer<String> {

    private final ObjectMapper objectMapper;

    public ObjectMapperReportSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(DiagnosticsReport report) throws ReportSerializationException {
        try {
            JsonNode node = report.root().accept(new NodeToJacksonVisitor());
            return this.objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(node);
        } catch (JacksonException e) {
            throw new ReportSerializationException(e);
        }
    }
}
