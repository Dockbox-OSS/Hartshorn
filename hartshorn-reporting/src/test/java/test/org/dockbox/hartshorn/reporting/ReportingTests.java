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

package test.org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.UseReporting;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer.JsonReportSerializer;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.annotations.Inject;

@UseReporting
@HartshornIntegrationTest(includeBasePackages = false)
public class ReportingTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    void testSystemReporterCreatesNonNullReport() {
        // Given
        Reportable configurable = applicationContext.get(Reportable.class);
        DiagnosticsReportCollector collector = applicationContext.get(DiagnosticsReportCollector.class);

        // When
        DiagnosticsReport report = collector.report(configurable);
        Node<?> root = report.root();

        // Then
        Assertions.assertNotNull(root);
    }

    @Test
    void testSystemReporterCreatesNonNullReportWithCustomReportable() throws ReportSerializationException {
        // Given
        Reportable configurable = applicationContext.get(Reportable.class);
        DiagnosticsReportCollector collector = applicationContext.get(DiagnosticsReportCollector.class);

        // When
        DiagnosticsReport report = collector.report(configurable);
        String serialized = report.serialize(new JsonReportSerializer());

        // Then
        Assertions.assertNotNull(serialized);
        Assertions.assertFalse(serialized.isEmpty());
    }
}
