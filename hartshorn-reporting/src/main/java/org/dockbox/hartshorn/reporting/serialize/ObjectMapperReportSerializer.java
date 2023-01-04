/*
 * Copyright 2019-2023 the original author or authors.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.ReportSerializer;

public abstract class ObjectMapperReportSerializer implements ReportSerializer<String> {

    @Override
    public String serialize(final DiagnosticsReport report) throws ReportSerializationException {
        try {
            final JsonNode node = report.root().accept(new NodeToJacksonVisitor());
            return this.objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (final JsonProcessingException e) {
            throw new ReportSerializationException(e);
        }
    }

    protected abstract ObjectMapper objectMapper();

    public static class XMLReportSerializer extends ObjectMapperReportSerializer {

        @Override
        protected ObjectMapper objectMapper() {
            return new XmlMapper();
        }
    }

    public static class JsonReportSerializer extends ObjectMapperReportSerializer {

        @Override
        protected ObjectMapper objectMapper() {
            return new JsonMapper();
        }
    }

}
