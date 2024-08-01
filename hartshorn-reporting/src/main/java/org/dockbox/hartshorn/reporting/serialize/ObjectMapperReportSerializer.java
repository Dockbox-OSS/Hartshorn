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

package org.dockbox.hartshorn.reporting.serialize;

import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.ReportSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * A {@link ReportSerializer} which uses Jackson's {@link ObjectMapper} to serialize a {@link DiagnosticsReport} to a
 * specific format.
 *
 * @see DiagnosticsReport
 * @see ReportSerializer
 * @see ObjectMapper
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class ObjectMapperReportSerializer implements ReportSerializer<String> {

    @Override
    public String serialize(DiagnosticsReport report) throws ReportSerializationException {
        try {
            JsonNode node = report.root().accept(new NodeToJacksonVisitor());
            return this.objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new ReportSerializationException(e);
        }
    }

    /**
     * Returns the {@link ObjectMapper} that is used to serialize the report. The returned value is not required to be
     * thread-safe. The returned value is not required to be the same instance on each invocation.
     *
     * @return the object mapper to use
     */
    protected abstract ObjectMapper objectMapper();

    /**
     * A {@link ReportSerializer} that serializes a {@link DiagnosticsReport} to XML. This serializer uses Jackson's
     * {@link XmlMapper} to serialize the report.
     *
     * @see DiagnosticsReport
     * @see ReportSerializer
     * @see ObjectMapper
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class XMLReportSerializer extends ObjectMapperReportSerializer {

        @Override
        protected ObjectMapper objectMapper() {
            return new XmlMapper();
        }
    }

    /**
     * A {@link ReportSerializer} that serializes a {@link DiagnosticsReport} to JSON. This serializer uses Jackson's
     * {@link JsonMapper} to serialize the report.
     *
     * @see DiagnosticsReport
     * @see ReportSerializer
     * @see ObjectMapper
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class JsonReportSerializer extends ObjectMapperReportSerializer {

        @Override
        protected ObjectMapper objectMapper() {
            return new JsonMapper();
        }
    }
}
