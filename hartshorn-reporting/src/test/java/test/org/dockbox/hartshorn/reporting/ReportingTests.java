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

package test.org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.application.HartshornApplication;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.UseStaticBinding;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.ReportSerializationException;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer.JsonReportSerializer;
import org.dockbox.hartshorn.util.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UseStaticBinding
public class ReportingTests {

    @Test
    void testSystemReporterCreatesNonNullReport() {
        // Given
        final ApplicationContext applicationContext = HartshornApplication.create(ReportingTests.class);
        final Reportable configurable = applicationContext.get(Reportable.class);
        final DiagnosticsReportCollector collector = applicationContext.get(DiagnosticsReportCollector.class);

        // When
        final DiagnosticsReport report = collector.report(configurable);
        final Node<?> root = report.root();

        // Then
        Assertions.assertNotNull(root);
    }

    @Test
    void testSystemReporterCreatesNonNullReportWithCustomReportable() throws ReportSerializationException {
        // Given
        final ApplicationContext applicationContext = HartshornApplication.create(ReportingTests.class);
        final Reportable configurable = applicationContext.get(Reportable.class);
        final DiagnosticsReportCollector collector = applicationContext.get(DiagnosticsReportCollector.class);

        // When
        final DiagnosticsReport report = collector.report(configurable);
        final String serialized = report.serialize(new JsonReportSerializer());

        // Then
        Assertions.assertNotNull(serialized);
        Assertions.assertFalse(serialized.isEmpty());
    }
}
