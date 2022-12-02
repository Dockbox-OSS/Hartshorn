/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.reporting.system;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class SystemDiagnosticsReporter implements ConfigurableDiagnosticsReporter<SystemReportingConfiguration>, CategorizedDiagnosticsReporter {

    public static final String SYSTEM_CATEGORY = "system";

    private final SystemReportingConfiguration configuration = new SystemReportingConfiguration();

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        if (this.configuration.includeUptime()) {
            // TODO: Convert to human readable format
            collector.property("uptime").write(runtimeMXBean.getUptime());
        }
        if (this.configuration.includeJavaDiagnostics()) {
            collector.property("java").write(new JavaDiagnosticsReporter());
        }
        if (this.configuration.includeJvmDiagnostics()) {
            collector.property("jvm").write(new JVMDiagnosticsReporter(this.configuration.includeJvmFlags()));
        }
        if (this.configuration.includeOsDiagnostics()) {
            collector.property("os").write(new OSDiagnosticsReporter());
        }
        if (this.configuration.includeDeviceName()) {
            collector.property("device").write(runtimeMXBean.getName().split("@")[1]);
        }
        if (this.configuration.includeProcessId()) {
            collector.property("pid").write(runtimeMXBean.getPid());
        }
        if (this.configuration.includeResponsibleServiceOrUser()) {
            final String user = System.getProperty("user.name");
            collector.property("user").write(user);
        }
        if (this.configuration.includeMemoryUsage()) {
            collector.property("memory").write(new MemoryUsageDiagnosticsReporter());
        }
    }

    @Override
    public SystemReportingConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public String category() {
        return SYSTEM_CATEGORY;
    }
}
